package api.framework.tasks;

import api.Configuration;
import api.framework.ScriptTask;
import api.general.Timing;
import xobot.script.methods.Packets;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Skills;
import xobot.script.wrappers.interactive.Item;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Eating extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        return Skills.getCurrentLevel(Skills.CONSTITUTION) < Configuration.EAT_AT;
    }

    @Override
    public int perform()
    {
        Item item = Inventory.getItem(Configuration.FOOD_ID);
        if (item != null)
        {
            int hp = Skills.getCurrentLevel(Skills.CONSTITUTION);
            Packets.sendAction(74, item.getID(), item.getSlot(), 3214);
            /**
             * to prevent spam eating
             */
            Timing.sleep(() -> Skills.getCurrentLevel(Skills.CONSTITUTION) != hp, 3000);
        }
        else
        {
            Packets.sendAction(315, 0, 0, 12856);
            return -1;
        }
        return 200;
    }

    @Override
    public int getPriority()
    {
        return 5;
    }
}