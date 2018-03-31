package api.task.impl;

import api.task.ScriptTask;
import xobot.script.methods.GroundItems;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.wrappers.interactive.GroundItem;
import xobot.script.wrappers.interactive.Item;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/25/2018.
 */
public class Looter extends ScriptTask
{
    private final ArrayList<GroundItem> groundItems = new ArrayList<>();

    @Override
    public boolean canPerform()
    {
        groundItems.clear();
        for (GroundItem item : GroundItems.getAll())
        {
            if (item != null && context.getFighterProfile().getLootIDs().contains(item.getItem().getID()) && item.getLocation().isReachable())
            {
                groundItems.add(item);
            }
        }
        return groundItems.size() > 0;
    }

    @Override
    public int perform()
    {
        groundItems.forEach(groundItem ->
        {
            if (Inventory.isFull())
            {
                final Item vialOrFood = Inventory.getItem(229, context.getFighterProfile().getFoodID());
                if (vialOrFood != null)
                {
                    if (vialOrFood.hasAction("Eat"))
                    {
                        vialOrFood.interact("eat");
                    }
                    else
                    {
                        vialOrFood.interact("drop");
                    }
                }
            }
            //if the bot managed to make room for the item
            if (Time.sleep(() -> !Inventory.isFull(), 3000))
            {
                int oldCount = Inventory.getRealCount();
                groundItem.getItem().interact("take");
                Time.sleep(new Callable<Boolean>()
                {
                    @Override
                    public Boolean call() throws Exception
                    {
                        return Inventory.getRealCount() > oldCount;
                    }
                }, 7500);
            }
        });
        return 50;
    }
}
