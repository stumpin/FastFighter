package api.task.impl;

import api.task.ScriptTask;
import xobot.script.methods.input.KeyBoard;
import xobot.script.methods.tabs.*;
import xobot.script.util.Time;
import xobot.script.wrappers.interactive.Item;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/25/2018.
 */
public class Consumer extends ScriptTask {

    private ConsumptionType currentType;

    @Override
    public boolean canPerform() {
        boolean needConsume = false;
        /*
         * prioritize eating
         */
        if (Skills.CONSTITUTION.getCurrentLevel() < context.getFighterProfile().getEatAt()) {
            currentType = ConsumptionType.HEAL;
            needConsume = true;
        } else if (xobot.script.methods.tabs.Prayer.getPointPercentage() < 20) {
            currentType = ConsumptionType.RESTORE;
            needConsume = true;
        }
        return needConsume;
    }

    @Override
    public int perform() {
        return currentType.consume() ? 50 : -1;
    }

    public enum ConsumptionType implements Consume {
        HEAL("eat"),
        RESTORE("drink");

        private String action;
        private int[] ids;

        ConsumptionType(String action) {
            this.action = action;
        }

        public boolean consume() {
            Item item = Inventory.getItem(ids);
            if (item != null) {
                item.interact(action);
                Time.sleep(750);
                return true;
            } else {
                KeyBoard.typeWord("::home", true);
                return false;
            }
        }

        public String getAction() {
            return action;
        }

        public void setIDs(int... ids) {
            this.ids = ids;
        }
    }

    public interface Consume {
        String getAction();

        boolean consume();
    }
}
