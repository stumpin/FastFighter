import api.ScriptContext;
import xobot.script.methods.NPCs;
import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.interactive.NPC;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/16/2018.
 */
public class GUI extends JFrame
{
    private boolean completed;
    private final ScriptContext context;
    private final DefaultListModel allNPCsModel;
    private final DefaultListModel myNPCsModel;
    private final DefaultTableModel lootModel;
    private final JTable lootTable;
    private final HashMap<JCheckBox, Prayer.Prayers> prayerMap;
    private final JTextField foodId;
    private final JSlider eatHP;
    private final JLabel eat;

    public GUI(final ScriptContext context)
    {
        super("Alora Fast Fighter");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setPreferredSize(new Dimension(550, 425));
        this.context = context;
        completed = false;
        allNPCsModel = new DefaultListModel();
        myNPCsModel = new DefaultListModel();
        final String[] columns = {
                "ID", "Stackable"
        };
        final Object[][] data = new Object[][]{
                {null, false}

        };
        lootModel = new DefaultTableModel(data, columns);
        lootTable = new JTable(lootModel)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Class getColumnClass(int column)
            {
                switch (column)
                {
                    case 0:
                        return Integer.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        prayerMap = new HashMap<>();
        foodId = new JTextField("385");
        eatHP = new JSlider(5, 90, 50);
        eat = new JLabel("Eating at: " + context.getFighterProfile().getEatAt());
        addMenuBar();
        addComponents();
        this.pack();
        this.setLocationRelativeTo(getOwner());
    }

    public void addMenuBar()
    {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu presets = new JMenu("Presets");

        final JMenuItem save = new JMenuItem("Save", new ImageIcon(ScriptContext.loadResourceImage("https://i.imgur.com/FOOCAMI.png", 18, 18)));
        save.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String name = JOptionPane.showInputDialog(null, "Enter profile name:", "Profile Saver", JOptionPane.PLAIN_MESSAGE);
                if (name != null && name.length() > 0)
                {
                    //before dumping profile, update the lists that don't update via listeners
                    updateLootList();
                    updatePrayerList();

                    ScriptContext.dumpProfile(context.getFighterProfile(), name);
                }
            }
        });

        final JMenuItem load = new JMenuItem("Load", new ImageIcon(ScriptContext.loadResourceImage("https://i.imgur.com/H0qH08L.png", 18, 18)));
        load.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final JFileChooser saveChooser = new JFileChooser(ScriptContext.getXobotPath() + "\\FastFighterProfiles");
                if (saveChooser.showOpenDialog(null) == JFileChooser.OPEN_DIALOG)
                {
                    context.setFighterProfile(ScriptContext.loadProfile(saveChooser.getSelectedFile()));
                    ///////// UPDATE THE GUI \\\\\\\\\\\\\
                    myNPCsModel.clear();
                    context.getFighterProfile().getNpcIDs().forEach(id -> myNPCsModel.addElement(id));

                    lootModel.setRowCount(0);
                    context.getFighterProfile().getLootIDs().forEach(id ->
                    {
                        Object[] o = new Object[]{
                                id, false
                        };
                        ((DefaultTableModel) lootTable.getModel()).addRow(o);
                    });

                    foodId.setText(String.valueOf(context.getFighterProfile().getFoodID()));
                    eat.setText("Eating at: " + context.getFighterProfile().getEatAt());
                    eatHP.setValue(context.getFighterProfile().getEatAt());

                    prayerMap.forEach((box, prayer) ->
                    {
                        if (context.getFighterProfile().getDesiredPrayers().contains(prayer))
                        {
                            box.setSelected(true);
                        }
                        else
                        {
                            box.setSelected(false);
                        }
                    });
                    context.getFighterProfile().getDesiredPrayers().clear();
                }
            }
        });

        presets.add(save);
        presets.add(load);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(presets);

        this.add(menuBar, BorderLayout.NORTH);
    }

    private void addComponents()
    {
        final JTabbedPane guiPane = new JTabbedPane();

        final JButton start = new JButton("Start Script");
        start.setToolTipText("Start the script");
        start.addActionListener((ActionEvent e) ->
        {
            completed = true;

            prayerMap.forEach((box, prayer) ->
            {
                if (box.isSelected())
                {
                    context.getFighterProfile().getDesiredPrayers().add(prayer);
                }
            });

            //update the loot list again
            updateLootList();

            GUI.this.dispose();
            GUI.this.setVisible(false);
        });

        //////////////////////////////////////////////////////////////////////////////

        final JPanel fightingPanel = new JPanel();
        fightingPanel.setLayout(null);
        fightingPanel.setBorder(BorderFactory.createTitledBorder("NPC Selector"));

        final JList allNPCsList = new JList(allNPCsModel);
        final JList myNPCsList = new JList(myNPCsModel);

        myNPCsList.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                    final Object value = myNPCsList.getSelectedValue();
                    if (value != null)
                    {
                        myNPCsModel.removeElement(value);
                    }
                }
            }
        });

        allNPCsList.addListSelectionListener((ListSelectionEvent e) -> myNPCsList.clearSelection());
        myNPCsList.addListSelectionListener((ListSelectionEvent e) -> allNPCsList.clearSelection());

        final JScrollPane allNPCs = new JScrollPane(allNPCsList);
        allNPCs.setBounds(15, 20, 235, 250);
        fightingPanel.add(allNPCs);

        final JScrollPane myNPCs = new JScrollPane(myNPCsList);
        myNPCs.setBounds(290, 20, 230, 250);
        fightingPanel.add(myNPCs);

        final JButton npcLoader = new JButton("Load NPCs");
        npcLoader.setBounds(75, 285, 125, 27);
        npcLoader.setToolTipText("Loads cached NPCs around local player");
        npcLoader.addActionListener((ActionEvent e) ->
        {
            allNPCsModel.clear();

            for (NPC npc : NPCs.getAll())
            {
                /*
                 * prevents 1, prevents duplicates
                 */
                if (npc != null && npc.getId() != 1 && !allNPCsModel.contains(npc.getId()))
                {
                    allNPCsModel.addElement(npc.getId());
                }
            }
        });
        fightingPanel.add(npcLoader);

        final JButton shifter = new JButton(new ImageIcon(ScriptContext.loadResourceImage("https://i.imgur.com/g71i0Kj.png", 18, 18)));
        shifter.setBounds(250, 285, 40, 27);
        shifter.setToolTipText("Shift over an npc to your list");
        shifter.addActionListener((ActionEvent e) ->
        {
            if (!allNPCsList.isSelectionEmpty())
            {
                final Object value = allNPCsList.getSelectedValue();
                if (value != null &&  !myNPCsModel.contains(value))
                {
                    myNPCsModel.addElement(value);
                    context.getFighterProfile().getNpcIDs().add((int) value);
                }
            }
        });
        fightingPanel.add(shifter);

        final JButton npcClearer = new JButton("Clear all");
        npcClearer.setBounds(340, 285, 125, 27);
        npcClearer.setToolTipText("Clear all elements in NPC list");
        npcClearer.addActionListener((ActionEvent e) ->
        {
            myNPCsModel.clear();
            context.getFighterProfile().getNpcIDs().clear();
        });
        fightingPanel.add(npcClearer);

        //////////////////////////////////////////////////////////

        final JPanel lootingPanel = new JPanel();
        lootingPanel.setLayout(new BorderLayout());
        lootingPanel.setBorder(BorderFactory.createTitledBorder("Loot list"));
        lootTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        lootTable.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DELETE && lootTable.getSelectedRow() != -1)
                {
                    lootModel.removeRow(lootTable.getSelectedRow());
                }
            }
        });

        final JScrollPane lootPane = new JScrollPane(lootTable);
        lootingPanel.add(lootPane, BorderLayout.CENTER);

        final JButton adder = new JButton(new ImageIcon(ScriptContext.loadResourceImage("https://i.imgur.com/6ueaY1k.png", 18, 18)));
        adder.setOpaque(false);
        adder.setFocusable(false);
        adder.setToolTipText("Add a row");
        adder.addActionListener((ActionEvent e) ->
        {
            Object[] o = new Object[] {
                    null, false
            };
            ((DefaultTableModel) lootTable.getModel()).addRow(o);
        });
        lootingPanel.add(adder, BorderLayout.AFTER_LAST_LINE);

        /////////////////////////////////////////////////////////////////////////////

        final JPanel miscPanel = new JPanel();
        miscPanel.setLayout(null);
        miscPanel.setBorder(BorderFactory.createTitledBorder("Prayer / Consumption"));

        final JPanel prayerBox = new JPanel();
        prayerBox.setLayout(new BoxLayout(prayerBox, BoxLayout.Y_AXIS));

        Arrays.stream(Prayer.Prayers.values()).forEach(entry ->
        {
            JCheckBox box = new JCheckBox(entry.getName());
            prayerMap.put(box, entry);
            prayerBox.add(box);
        });

        JScrollPane prayerPane = new JScrollPane();
        prayerPane.setViewportView(prayerBox);
        prayerPane.setBounds(15, 20, 235, 250);

        miscPanel.add(prayerPane);

        final JLabel food = new JLabel("Food ID");
        food.setBounds(330, 20, 100, 20);
        miscPanel.add(food);

        foodId.setBounds(380, 20, 60, 20);
        foodId.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                update();
            }
            public void removeUpdate(DocumentEvent e)
            {
                update();
            }
            public void insertUpdate(DocumentEvent e)
            {
                update();
            }

            public void update()
            {
                final String id = foodId.getText();
                if (id != null && id.length() > 0 && isNumber(id))
                {
                    context.getFighterProfile().setFoodID(Integer.valueOf(foodId.getText()));
                }
            }
        });
        miscPanel.add(foodId);

        eat.setBounds(330, 100, 125, 20);
        miscPanel.add(eat);

        eatHP.setMinorTickSpacing(1);
        eatHP.setMajorTickSpacing(10);
        eatHP.setBounds(275, 120, 235, 50);
        eatHP.setPaintTicks(true);
        eatHP.setPaintLabels(true);
        eatHP.addChangeListener((ChangeEvent e) ->
        {
            context.getFighterProfile().setEatAt(eatHP.getValue());
            eat.setText("Eating at: " + context.getFighterProfile().getEatAt());
        });
        miscPanel.add(eatHP);

        guiPane.add("Fighting", fightingPanel);
        guiPane.add("Looting", lootingPanel);
        guiPane.add("Misc", miscPanel);

        this.add(guiPane, BorderLayout.CENTER);
        this.add(start, BorderLayout.SOUTH);
    }

    private void updateLootList()
    {
        context.getFighterProfile().getLootIDs().clear();
        for (int i = 0; i < lootTable.getRowCount(); i++)
        {
            Object value = lootTable.getModel().getValueAt(i, 0);
            if (value != null)
            {
                context.getFighterProfile().getLootIDs().add((int) value);
            }
        }
    }

    private void updatePrayerList()
    {
        context.getFighterProfile().getDesiredPrayers().clear();
        prayerMap.forEach((box, prayer) ->
        {
            if (box.isSelected())
            {
                context.getFighterProfile().getDesiredPrayers().add(prayer);
            }
        });
    }

    public boolean isCompleted()
    {
        return completed;
    }

    private boolean isNumber(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}
