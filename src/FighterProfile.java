import xobot.script.wrappers.Tile;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 10/7/2018.
 */
public class FighterProfile implements Serializable {

    private boolean boosters = true;
    private final ArrayList<Tile> safeSpots = new ArrayList<>();
    private final ArrayList<Integer> ids = new ArrayList<>();
    private final RSLootItem[] items = {
            new RSLootItem(283, true)
    };


    public FighterProfile() {

    }


    public static void dumpProfile(FighterProfile profile, String profileName, String name) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(name + ".serialized");
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
}
