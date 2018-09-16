package api.structures;

import xobot.script.methods.tabs.Prayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/16/2018.
 */
public class FighterProfile implements Serializable {

    private final ArrayList<Integer> lootIDs;
    private final ArrayList<Integer> npcIDs;
    private final ArrayList<Prayer.Prayers> prayers;
    private int foodID;
    private int eatAt;

    public FighterProfile() {
        lootIDs = new ArrayList<>();
        npcIDs = new ArrayList<>();
        prayers = new ArrayList<>();
        foodID = 385;
        eatAt = 50;
    }

    public ArrayList<Integer> getLootIDs() {
        return lootIDs;
    }

    public ArrayList<Integer> getNpcIDs() {
        return npcIDs;
    }

    public ArrayList<Prayer.Prayers> getDesiredPrayers() {
        return prayers;
    }

    public int getFoodID() {
        return foodID;
    }

    public int getEatAt() {
        return eatAt;
    }

    public void setFoodID(int newID) {
        foodID = newID;
    }

    public void setEatAt(int newHealth) {
        eatAt = newHealth;
    }
}
