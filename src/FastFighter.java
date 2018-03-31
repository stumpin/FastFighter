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

@Manifest(authors = { "Kumalo" }, name = "Soulplay Fast ", description = "Fights anything, loots anything")

public class FastFighter extends ActiveScript implements PaintListener
{
    private Timer scriptTimer;
    private ScriptContext scriptContext;
    private TaskManager manager;
    private final double VERSION = 1.3;
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

        final GUI scriptGUI = new GUI(scriptContext);
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
        return Game.isLoggedIn() ? manager.getIdealTask().perform() : 500;
    }


    @Override
    public void repaint(Graphics g)
    {
        g.setColor(TRANS_BLACK);
        g.fillRect(0, 305, 516, 33);

        g.setColor(Color.WHITE);
        g.drawString("Run time: " + scriptTimer.toElapsedString(), 20, 327);
        g.drawString("Fast Fighter V" + VERSION, 220, 327);
        g.drawString("Task: " + manager.getCurrentTask().getName(), 420, 327);

        final NPC target = scriptContext.getTargetNpc();
        if (target != null)
        {
            target.getLocation().draw(g, target.isDead() ? Color.black : scriptContext.getTileColor());
        }
    }
}

