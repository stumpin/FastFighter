import api.ScriptContext;
import api.task.TaskManager;
import api.task.impl.Consumer;
import api.task.impl.Fighter;
import api.task.impl.Looter;
import api.task.impl.Prayer;
import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Game;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.Item;
import xobot.script.wrappers.interactive.NPC;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/16/2018.
 */

@Manifest(authors = { "Jake" }, name = "Alora Fast Fighter", description = "Fights anything, loots anything")

public class FastFighter extends ActiveScript implements PaintListener
{
    private Timer scriptTimer;
    private GUI scriptGUI;
    private ScriptContext scriptContext;
    private TaskManager manager;
    private final double VERSION = 1.1;
    private final Color TRANS_BLACK = new Color(0, 0, 0, 127);

    @Override
    public boolean onStart()
    {
        final File profiles = new File(ScriptContext.getXobotPath() + "FastFighterProfiles");

        if (!profiles.exists())
        {
            profiles.mkdir();
        }

        scriptContext = new ScriptContext();

        scriptGUI = new GUI(scriptContext);
        scriptGUI.setVisible(true);

        while (scriptGUI.isVisible())
        {
            Time.sleep(250);
        }

        manager = new TaskManager(scriptContext, new Consumer(), new Prayer(), new Fighter(), new Looter());
        scriptTimer = new Timer();

        return scriptGUI.isCompleted();
    }

    @Override
    public int loop()
    {
        if (Game.isLoggedIn())
        {
            manager.getIdealTask().perform();
            //Arrays.stream(Inventory.getAll(577, 1011)).filter(Item -> (Item != null)).forEach(Item -> Item.interact("open"));
            Item casket = Inventory.getItem(2717, 2720, 2726);
            if (casket != null)
            {
                casket.interact("open");
                Time.sleep(1200);
            }
        }

        return 50;
    }


    @Override
    public void repaint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(TRANS_BLACK);
        g2.fillRect(0, 305, 516, 33);

        g2.setColor(Color.WHITE);
        g2.drawString("Run time: " + scriptTimer.toElapsedString(), 20, 327);

        final NPC target = scriptContext.getTargetNpc();
        if (target != null)
        {
            target.getLocation().draw(g2, target.isDead() ? Color.black : scriptContext.getTileColor());
        }
    }
}

