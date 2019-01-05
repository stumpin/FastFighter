import xobot.script.methods.tabs.Prayer;
import xobot.script.wrappers.Tile;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/7/2018.
 */
public class FighterProfile implements Serializable {

    public boolean potions = false, renewals = false;
    public int eat = 50, radius = 0, delay = 0;
    public ArrayList<Tile> safeSpots = new ArrayList<>();
    public ArrayList<Integer> ids = new ArrayList<>();
    public ArrayList<Prayer.Prayers> prayers = new ArrayList<>();
    public ArrayList<RSLootItem> lootItems = new ArrayList<>();

    public static void dumpProfile(FighterProfile profile, String name) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(getXobotPath() + "FastFighterProfiles" + System.getProperty("file.separator") + name + ".serialized");
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(profile);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FighterProfile loadProfile(File file) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            final FighterProfile profile = (FighterProfile) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getXobotPath() {
        final StringBuilder builder = new StringBuilder();
        final String separator = System.getProperty("file.separator");
        builder.append(System.getProperty("user.home")).append(separator).append("Documents").append(separator).append("XoBot").append(separator);
        return builder.toString();
    }
}
