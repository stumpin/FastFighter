import api.Configuration;
import api.framework.ScriptTask;
import api.framework.TaskManager;
import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.util.Time;

import api.framework.tasks.*;
import xobot.script.util.Timer;

import java.awt.*;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
@Manifest(authors = { "Kumalo" }, name = "Fast Fighter", description = "Fights anything, loots anything")

public class FastFighter extends ActiveScript implements PaintListener
{
    private GUI menu;
    private TaskManager manager;
    private ScriptTask currentTask;
    private Timer startTime;

    @Override
    public boolean onStart()
    {
        menu = new GUI();
        menu.setVisible(true);

        while (menu.isVisible())
        {
            Time.sleep(100);
        }

        startTime = new Timer();

        for (int i = 0; i < menu.getMyNPCsList().getModel().getSize(); i++)
        {
            Object value = menu.getMyNPCsList().getModel().getElementAt(i);
            if (value != null)
            {
                Configuration.NPC_IDS.add((int) value);
            }
        }

        for (int i = 0; i < menu.getLootTable().getRowCount(); i++)
        {
            Object value = menu.getLootTable().getModel().getValueAt(i, 0);
            if (value != null)
            {
                Configuration.LOOT_IDS.add((int) value);
            }
        }

        manager = new TaskManager(new Eating(), new Restoring(), new Praying(), new Fighting(), new Looting());

        return menu.isCompleted();
    }

    @Override
    public int loop()
    {
        currentTask = manager.getCurrentTask();
        return currentTask.perform();
    }

    @Override
    public void repaint(Graphics g)
    {
        g.setColor(new Color(0, 0, 0, 127));
        g.fillRect(0, 305, 516, 33);
        g.setColor(Color.WHITE);
        g.drawString("Run time: " + startTime.toElapsedString(), 20, 325);
        g.drawString("Fast Fighter V" + Configuration.VERSION, 200, 325);
        g.drawString("Task: " + currentTask.getName(), 400, 325);
    }
}
