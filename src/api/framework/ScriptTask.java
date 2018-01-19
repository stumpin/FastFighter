package api.framework;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public abstract class ScriptTask
{
    private int priority;

    public ScriptTask()
    {
        this.priority = 0;
    }

    /**
     * Determines if the ScriptTask can be performed
     *
     * @return true if the task can be performed
     */
    public abstract boolean canPerform();

    /**
     * Performs the ScriptTask
     */
    public abstract int perform();

    /**
     * @return priority of the task
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Returns the name of the task
     *
     * @return task name
     */
    public String getName()
    {
        return getClass().getSimpleName();
    }
}
