package api.task.impl;

import api.task.ScriptTask;
import xobot.script.methods.tabs.Prayer.Prayers;
import xobot.script.util.Time;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/25/2018.
 */
public class Prayer extends ScriptTask {

    private final ArrayList<Prayers> prayers = new ArrayList<>();

    @Override
    public boolean canPerform() {
        prayers.clear();
        for (Prayers prayer : context.getFighterProfile().getDesiredPrayers()) {
            if (!prayer.isActivated()) {
                prayers.add(prayer);
            }
        }
        return prayers.size() > 0;
    }

    @Override
    public int perform() {
        Time.sleep(500); //in case the bot just logged in
        prayers.forEach(prayer -> {
            prayer.Activate();
            Time.sleep(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return prayer.isActivated();
                }
            }, 3000);
        });
        return 50;
    }
}
