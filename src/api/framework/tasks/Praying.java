package api.framework.tasks;

import api.Configuration;
import api.framework.ScriptTask;

import api.general.Timing;
import xobot.script.methods.Game;
import xobot.script.methods.tabs.Prayer;
import xobot.script.util.Time;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/12/2018.
 */
public class Praying extends ScriptTask
{
    Prayer.Prayers disabled;

    @Override
    public boolean canPerform()
    {
        disabled = null;
        for (Prayer.Prayers prayer : Configuration.PRAYERS)
        {
            /**
             * prevents it from spam turning it on, in the event of 0 prayer
             */
            if (!prayer.isActivated())
            {
                disabled = prayer;
                return Game.isLoggedIn() && Prayer.getRemainingPoints() > 0;
            }
        }
        return false;
    }

    @Override
    public int perform()
    {
        if (disabled != null)
        {
            Time.sleep(300);
            disabled.Activate();
            Timing.sleep(() -> disabled.isActivated(), 2500);
        }
        return 200;
    }

    @Override
    public int getPriority()
    {
        return 3;
    }
}