package api.framework.tasks;

import api.Configuration;
import api.framework.ScriptTask;
import api.general.Timing;
import xobot.script.methods.Packets;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.interactive.Item;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/13/2018.
 */
public class Restoring extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        return Prayer.getPointPercentage() < 20;
    }

    @Override
    public void perform()
    {
        Item item = Inventory.getItem(Configuration.RESTORE_IDS);
        if (item != null)
        {
            int pray = Prayer.getRemainingPoints();
            Packets.sendAction(74, item.getID(), item.getSlot(), 3214);
            /**
             * to prevent spam eating
             */
            Timing.sleep(() -> Prayer.getRemainingPoints() != pray, 3000);
        }
        else
        {
            Packets.sendAction(315, 0, 0, 12856);
        }
    }

    @Override
    public int getPriority()
    {
        return 4;
    }
}
