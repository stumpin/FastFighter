import api.Configuration;
import api.io.ImageHelper;
import xobot.script.methods.NPCs;
import xobot.script.methods.Projectiles;
import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.interactive.NPC;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class GUI extends JFrame
{
    private DefaultListModel allNPCsModel = new DefaultListModel();
    private DefaultListModel myNPCsModel = new DefaultListModel();

    private JList myNPCsList;
    private JList allNPCsList;

    private JTable lootTable;

    private boolean completed = false;
    private JTabbedPane tabbedPane = new JTabbedPane();

    private JPanel fightingPanel = new JPanel();
    private JPanel eatingPanel = new JPanel();
    private JPanel lootingPanel = new JPanel();
    private JPanel prayerPanel = new JPanel();

    private HashMap<JCheckBox, Prayer.Prayers> prayerMap;

    public GUI()
    {
        super("Fast Fighter - Written by Kumalo - Powered by Xobot");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setPreferredSize(new Dimension(550, 375));

        /**
         * prevents both values from being selected at once
         */
        allNPCsList = new JList(allNPCsModel);
        allNPCsList.addListSelectionListener((ListSelectionEvent e) -> myNPCsList.clearSelection());

        myNPCsList = new JList(myNPCsModel);
        myNPCsList.addListSelectionListener((ListSelectionEvent e) -> allNPCsList.clearSelection());

        this.addComponents();
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void addComponents()
    {
        /**
         * Fighting panel
         */
        JScrollPane allNPCs = new JScrollPane(allNPCsList);
        allNPCs.setBounds(10, 20, 225, 200);

        JScrollPane myNPCs = new JScrollPane(myNPCsList);
        myNPCs.setBounds(300, 20, 225, 200);

        JLabel all = new JLabel("Loaded NPCs");
        all.setBounds(80, 2, 100, 20);

        JLabel yours = new JLabel("Your NPC List");
        yours.setBounds(355, 2, 100, 20);

        JButton load = new JButton("Load NPCs");
        load.setBounds(195, 250, 150, 20);
        load.setToolTipText("Loads cached NPCs around local player");
        load.addActionListener((ActionEvent e) ->
        {
            allNPCsModel.clear();
            NPC[] npcs = NPCs.getAll();

            for (NPC npc : npcs)
            {
                /**
                 * prevents 1, prevents duplicates
                 */
                if (npc != null && npc.getId() != 1 && !allNPCsModel.contains(npc.getId()))
                {
                    allNPCsModel.addElement(npc.getId());
                }
            }
        });

        ImageHelper helper = new ImageHelper();
        JButton shifter = new JButton(new ImageIcon(helper.scaleImage(helper.loadResourceImage("https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/Resize_horizontal_font_awesome.svg/1000px-Resize_horizontal_font_awesome.svg.png"), 20, 20)));
        shifter.setBounds(247, 120, 40, 20);
        shifter.setOpaque(false);
        shifter.setFocusable(false);
        shifter.setToolTipText("Shift over an npc to your list, or delete it from you list");
        shifter.addActionListener((ActionEvent e) ->
        {
            if (!allNPCsList.isSelectionEmpty())
            {
                if (!myNPCsModel.contains(allNPCsList.getSelectedValue()))
                {
                    myNPCsModel.addElement(allNPCsList.getSelectedValue());
                }
            }
            else if (!myNPCsList.isSelectionEmpty())
            {
                myNPCsModel.removeElement(myNPCsList.getSelectedValue());
            }
        });

        JButton start = new JButton("Start Script");
        start.setToolTipText("Start the script");
        start.addActionListener((ActionEvent e) ->
        {
            completed = true;

            prayerMap.forEach((box, prayer) ->
            {
                if (box.isSelected())
                {
                    Configuration.PRAYERS.add(prayer);
                }
            });

            GUI.this.dispose();
            GUI.this.setVisible(false);
        });

        fightingPanel.setLayout(null);
        fightingPanel.add(all);
        fightingPanel.add(yours);
        fightingPanel.add(allNPCs);
        fightingPanel.add(myNPCs);
        fightingPanel.add(load);
        fightingPanel.add(shifter);

        /**
         * Looting Panel
         */

        String[] columns = {
                "ID", "Stackable"
        };
        Object[][] data = new Object[][] {
                {null, false}

        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        lootTable = new JTable(model)
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

        JScrollPane lootPane = new JScrollPane(lootTable);

        JButton adder = new JButton(new ImageIcon(helper.scaleImage(helper.loadResourceImage("https://cdn1.iconfinder.com/data/icons/toolbar-signs/512/add-512.png"), 20, 20)));
        adder.setOpaque(false);
        adder.setFocusable(false);
        adder.setToolTipText("Add a row");
        adder.addActionListener((ActionEvent e) ->
        {
            Object[] o = new Object[]{
                        null, false
            };
            ((DefaultTableModel) lootTable.getModel()).addRow(o);
        });

        lootingPanel.setLayout(new BorderLayout());
        lootingPanel.add(lootPane, BorderLayout.CENTER);
        lootingPanel.add(adder, BorderLayout.LINE_END);

        /**
         * Eating panel
         */

        JSlider eatAt = new JSlider(20, 80);
        eatAt.setMinorTickSpacing(1);
        eatAt.setMajorTickSpacing(10);
        eatAt.setBounds(150, 50, 225, 50);
        eatAt.setPaintTicks(true);
        eatAt.setPaintLabels(true);

        Configuration.EAT_AT = eatAt.getValue();
        JLabel eat = new JLabel("Eating at: " + Configuration.EAT_AT);
        eat.setBounds(225, 25, 100, 20);
        eatAt.addChangeListener((ChangeEvent e) ->
        {
            Configuration.EAT_AT = eatAt.getValue();
            eat.setText("Eating at: " + Configuration.EAT_AT);
        });

        JLabel food = new JLabel("Food ID");
        food.setBounds(235, 125, 100, 20);

        JTextField foodId = new JTextField("385");
        foodId.setBounds(235, 150, 35, 20);
        foodId.addPropertyChangeListener((PropertyChangeEvent evt) -> Configuration.FOOD_ID = Integer.valueOf(foodId.getText()));

        eatingPanel.setLayout(null);
        eatingPanel.add(eatAt);
        eatingPanel.add(food);
        eatingPanel.add(foodId);
        eatingPanel.add(eat);

        /**
         * Prayer panel
         */

        JPanel prayerBox = new JPanel();
        prayerMap = new HashMap<>();
        prayerBox.setLayout(new BoxLayout(prayerBox, BoxLayout.Y_AXIS));

        Arrays.stream(Prayer.Prayers.values()).forEach(entry ->
        {
            JCheckBox box = new JCheckBox(entry.getName());
            prayerMap.put(box, entry);
            prayerBox.add(box);
        });

        JScrollPane prayerPane = new JScrollPane();

        prayerPane.setViewportView(prayerBox);
        prayerPane.setBounds(10, 20, 225, 200);

        prayerPanel.setLayout(null);
        prayerPanel.add(prayerPane);

        tabbedPane.add("Fighting", fightingPanel);
        tabbedPane.add("Looting", lootingPanel);
        tabbedPane.add("Eating", eatingPanel);
        tabbedPane.add("Prayer", prayerPanel);

        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(start, BorderLayout.SOUTH);
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public JList getMyNPCsList()
    {
        return myNPCsList;
    }

    public JTable getLootTable()
    {
        return lootTable;
    }
}
