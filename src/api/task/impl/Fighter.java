package api.task.impl;

import api.task.ScriptTask;
import xobot.script.methods.NPCs;
import xobot.script.methods.Players;
import xobot.script.util.Filter;
import xobot.script.util.Time;
import xobot.script.wrappers.interactive.Character;
import xobot.script.wrappers.interactive.NPC;
import xobot.script.wrappers.interactive.Player;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/25/2018.
 */
public class Fighter extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        return Players.getMyPlayer().getInteractingCharacter() == null;
    }

    @Override
    public int perform()
    {
        final NPC target = NPCs.getNearest(new Filter<NPC>()
        {
            @Override
            public boolean accept(NPC npc)
            {
                if (npc == null)
                {
                    return false;
                }
                final Character interacting = npc.getInteractingCharacter();

                return context.getFighterProfile().getNpcIDs().contains(npc.getId()) && npc.isReachable() && !npc.isDead() && (!npc.isInCombat() || (interacting != null && interacting.equals(Player.getMyPlayer())));
            }
        });
        if (target != null)
        {
            context.setTargetNpc(target);
            context.setTileColor(Color.YELLOW);
            target.interact("attack");
            context.setTileColor((Time.sleep(new Callable<Boolean>()
            {
                @Override
                public Boolean call() throws Exception
                {
                    return Players.getMyPlayer().getInteractingCharacter() != null;
                }
            }, 5000)) ? Color.GREEN : Color.RED);
        }
        return 50;
    }
}
