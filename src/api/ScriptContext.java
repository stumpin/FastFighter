package api;

import api.structures.FighterProfile;
import xobot.script.wrappers.interactive.NPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;


/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 3/7/2018.
 */
public class ScriptContext
{
    private FighterProfile profile;
    private NPC target;
    private Color tileColor;

    public static final int RESTORE_IDS[] = {
            3030, 3028, 3026, 3024, //restores
            143, 141, 139, 2434 //prayers
    };

    public ScriptContext()
    {
        profile = new FighterProfile();
        tileColor = Color.green;
    }

    public NPC getTargetNpc()
    {
        return target;
    }

    public FighterProfile getFighterProfile()
    {
        return profile;
    }

    public Color getTileColor()
    {
        return tileColor;
    }

    public void setTargetNpc(NPC npc)
    {
        target = npc;
    }

    public void setFighterProfile(FighterProfile profile)
    {
        this.profile = profile;
    }

    public void setTileColor(Color color)
    {
        tileColor = color;
    }

    /////////////////////// PUBLIC STATIC METHODS \\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String getXobotPath()
    {
        final StringBuilder builder = new StringBuilder();
        final String separator = System.getProperty("file.separator");
        builder.append(System.getProperty("user.home")).append(separator).append("Documents").append(separator).append("XoBot").append(separator);
        return builder.toString();
    }

    public static void dumpProfile(FighterProfile profile, String profileName)
    {
        try
        {
            final FileOutputStream fileOutputStream = new FileOutputStream(getXobotPath() + "FastFighterProfiles\\" + profileName + ".serialized");
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(profile);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static FighterProfile loadProfile(File file)
    {
        try
        {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            final FighterProfile profile = (FighterProfile) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return profile;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Image loadResourceImage(String path, int width, int height)
    {
        try
        {
            final Image img = ImageIO.read(new URL(path));
            if (img != null)
            {
                return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Source: " + path + "\nCaused by: " + e.getCause().toString(), "Error loading image", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
