package api.general;

import api.filters.Condition;
import xobot.script.util.Time;
import xobot.script.util.Timer;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Timing
{
    /**
     * Sleeps until condition has been met, or timeout has been reached
     * with waitPerLoop parameter
     *
     * @param condition
     * @param waitPerLoop
     * @param timeout
     * @return
     */
    public static boolean sleep(Condition condition, int waitPerLoop, int timeout)
    {
        Timer t = new Timer(timeout);
        while (t.isRunning())
        {
            if (condition.isValid())
            {
                return true;
            }
            Time.sleep(waitPerLoop);
        }
        return false;
    }
    /**
     * Sleeps until condition has been met, or timeout has been reached
     * @param condition
     * @param timeout
     * @return
     */
    public static boolean sleep(Condition condition, int timeout)
    {
        return sleep(condition, 100, timeout);
    }
}
