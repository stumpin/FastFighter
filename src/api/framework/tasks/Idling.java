package api.framework.tasks;

import api.framework.ScriptTask;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Idling extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        return true;
    }

    @Override
    public int perform()
    {
        return 200;
    }
}
