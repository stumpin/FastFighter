package api;

import xobot.script.methods.tabs.Prayer;

import java.util.ArrayList;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class Configuration
{
    public static final double VERSION = 1.1;

    public static ArrayList<Integer> LOOT_IDS = new ArrayList<>();

    static
    {
        LOOT_IDS.add(18778); //effigy
        LOOT_IDS.add(12163); //charm
        LOOT_IDS.add(12160); //charm
        LOOT_IDS.add(12158); //charm
        LOOT_IDS.add(12159); //charm
    }

    public static ArrayList<Prayer.Prayers> PRAYERS = new ArrayList<>();

    public static final int RESTORE_IDS[] = {
            3030, 3028, 3026, 3024,
    };

    public static int EAT_AT = 50;

    public static ArrayList<Integer> NPC_IDS = new ArrayList<>();

    public static int FOOD_ID = -1;
}
