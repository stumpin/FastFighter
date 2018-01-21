package api.io;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created by HP xw8400
 * Author: Jacob
 * Date: 1/2/2018.
 */
public class ImageHelper
{
    public Image scaleImage(Image img, int w, int h)
    {
        return (img != null) ? img.getScaledInstance(w, h, Image.SCALE_SMOOTH) : null;
    }

    public Image loadResourceImage(String path)
    {
        try
        {
            return ImageIO.read(Thread.currentThread().getContextClassLoader().getResource(path));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Source: " + path + "\nCaused by: " + e.getCause().toString(), "Error loading image", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
