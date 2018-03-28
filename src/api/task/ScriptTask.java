package api.task;

import api.ScriptContext;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public abstract class ScriptTask
{
    //protected for cleaner visibility in ScriptTask objects
    protected ScriptContext context;

    /**
     * Determines if the ScriptTask can be performed
     *
     * @return true if the task can be performed
     */
    public abstract boolean canPerform();

    /**
     * Performs the ScriptTask
     */
    public abstract void perform();

    /**
     * Returns the name of the task
     *
     * @return task name
     */
    public String getName()
    {
        return getClass().getSimpleName();
    }

    public void setContext(final ScriptContext context)
    {
        this.context = context;
    }
}
