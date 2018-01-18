package api.io;

import javax.imageio.ImageIO;
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
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    public Image loadResourceImage(String path)
    {
        try
        {
            return ImageIO.read(new URL(path));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
