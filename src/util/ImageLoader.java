package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

public class ImageLoader {
    public static ImageIcon load(String path, int width, int height) {
        try {
            URL url = ImageLoader.class.getResource("/" + path);
            if (url == null) return placeholder(width, height, path);
            Image img = Toolkit.getDefaultToolkit().getImage(url);
            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return placeholder(width, height, path);
        }
    }

    private static ImageIcon placeholder(int w, int h, String text) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.drawString(text, 5, h / 2);
        g.dispose();
        return new ImageIcon(img);
    }
}
