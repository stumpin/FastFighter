import api.Configuration;
import api.framework.ScriptTask;
import api.framework.TaskManager;
import api.framework.tasks.*;

import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.util.Timer;

import java.awt.*;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

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
    private ScriptTask currentTask = new Idling();
    private Timer startTime;
    private Font font;
    private final Color transBlack = new Color(0, 0, 0, 127);
    private final Color transBlue = new Color(25, 156, 255, 127);

    @Override
    public boolean onStart()
    {
        menu = new GUI();
        menu.setVisible(true);

        startTime = new Timer();
        manager = new TaskManager(new Eating(), new Restoring(), new Praying(), new Fighting(), new Looting());

        try
        {
            StringBuilder builder = new StringBuilder();
            builder.append(System.getProperty("user.home"));
            builder.append("\\Documents\\XoBot\\Scripts\\resources\\fonts\\Capture_it.ttf");

            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(builder.toString()));
            font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, 18);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public int loop()
    {
        if (menu.isCompleted())
        {
            currentTask = manager.getCurrentTask();
            return currentTask.perform();
        }
        return 250;
    }

    @Override
    public void repaint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (menu.isVisible())
        {
            menu.drawGrid(g2);
        }
        g2.setColor(transBlack);
        g2.fillRect(0, 305, 516, 33);
        g2.setColor(Color.WHITE);
        g2.drawString("Run time: " + startTime.toElapsedString(), 20, 325);
        g2.drawString("Version: " + Configuration.VERSION, 220, 325);
        g2.drawString("Task: " + currentTask.getName(), 420, 325);
        g2.setFont(font);
        g2.setColor(transBlue);
        g2.fillRect(0, 272, 140, 33);
        g2.fillArc(105, 272, 70, 66, 0, 90);
        g2.setColor(Color.WHITE);
        g2.drawString("Fast Fighter", 20, 295);
    }
}
