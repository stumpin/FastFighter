package api.framework;

import api.framework.tasks.*;
/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class TaskManager
{
    private ScriptTask[] tasks;

    public TaskManager(ScriptTask... tasks)
    {
        this.tasks = tasks;
    }

    /**
     * Gets the task that should be performed next
     *
     * @return current ScriptTask
     */
    public ScriptTask getCurrentTask()
    {
        ScriptTask current = new Idling();

        for (ScriptTask task: tasks)
        {
            if (task.canPerform() && task.getPriority() > current.getPriority())
            {
                current = task;
            }
        }
        return current;
    }

    public ScriptTask[] getTaskPool()
    {
        return tasks;
    }
}
