package api.framework.tasks;

import api.Configuration;
import api.framework.ScriptTask;
import api.general.Timing;
import xobot.script.methods.GroundItems;
import xobot.script.methods.tabs.Inventory;
import xobot.script.wrappers.interactive.GroundItem;
import xobot.script.wrappers.interactive.Item;

import java.util.ArrayList;


/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Looting extends ScriptTask
{
    ArrayList<GroundItem> groundItems = new ArrayList<>();

    @Override
    public boolean canPerform()
    {
        groundItems.clear();

        GroundItem[] items = GroundItems.getAll();
        for (GroundItem item : items)
        {
            if (item != null && Configuration.LOOT_IDS.contains(item.getItem().getID()) && item.getLocation().isReachable())
            {
                groundItems.add(item);
            }
        }

        return groundItems.size() > 0;
    }

    @Override
    public int perform()
    {
        /*
         * if inventory is full, attempt to make room...
         */
        if (Inventory.isFull())
        {
            Item vial = Inventory.getItem(229);
            if (vial != null)
            {
                vial.interact("Drop");
            }
            else
            {
                new Eating().perform();
            }
        }
        if (Timing.sleep(() -> !Inventory.isFull(), 2500))
        {
            int oldCount = Inventory.getRealCount();
            groundItems.get(0).getItem().interact("Take");
            Timing.sleep(() -> Inventory.getRealCount() > oldCount, 7500);
        }
        return 200;
    }

    @Override
    public int getPriority()
    {
        return 2;
    }
}
