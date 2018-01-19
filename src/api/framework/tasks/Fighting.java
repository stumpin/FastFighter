package api.framework.tasks;

import api.Configuration;
import api.framework.ScriptTask;
import api.general.Timing;
import xobot.script.methods.NPCs;
import xobot.script.methods.Players;
import xobot.script.wrappers.interactive.NPC;
import xobot.script.wrappers.interactive.Character;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Fighting extends ScriptTask
{
    @Override
    public boolean canPerform()
    {
        if (Players.getMyPlayer().isInCombat())
        {
            Character interacting = Players.getMyPlayer().getInteractingCharacter();
            return interacting != null && interacting.isDead();
        }
        return Players.getMyPlayer().getInteractingIndex() == -1;
    }

    @Override
    public int perform()
    {
        NPC target = NPCs.getNearest((NPC npc) -> npc != null && Configuration.NPC_IDS.contains(npc.getId()) && npc.isReachable() && !npc.isInCombat() && !npc.isDead());
        if (target != null)
        {
            target.interact("Attack");
            Timing.sleep(() -> Players.getMyPlayer().getInteractingIndex() != -1 || target.isInCombat(), 5000);
        }
        return 200;
    }

    @Override
    public int getPriority()
    {
        return 1;
    }
}
