package api.task.impl;

import api.task.ScriptTask;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/7/2018.
 */
public class Idler extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        return true;
    }

    @Override
    public void perform()
    {
        //< --- | DOES NOTHING | --- >\\
    }
}
