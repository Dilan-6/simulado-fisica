package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

/**
 * Utilidad para cargar y redimensionar imágenes desde recursos.
 * Si la imagen no se encuentra, genera un placeholder con el nombre del archivo.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class ImageLoader {
    /**
     * Carga una imagen desde recursos y la redimensiona al tamaño especificado.
     * 
     * @param path Ruta relativa del recurso (ej: "resources/ball.png")
     * @param width Ancho deseado en píxeles
     * @param height Alto deseado en píxeles
     * @return ImageIcon con la imagen cargada y redimensionada, o un placeholder si falla
     */
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

    /**
     * Genera un ImageIcon placeholder cuando no se puede cargar la imagen.
     * 
     * @param w Ancho del placeholder en píxeles
     * @param h Alto del placeholder en píxeles
     * @param text Texto a mostrar en el placeholder (normalmente el nombre del archivo)
     * @return ImageIcon con un rectángulo gris y el texto centrado
     */
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
