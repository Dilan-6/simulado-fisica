package view;

import javax.swing.*;
import java.awt.*;

/**
 * Vista de diálogo para mostrar resultados de cálculos.
 * Muestra texto formateado en un área de texto no editable con scroll.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class ResultView extends JDialog {
    /**
     * Construye un nuevo diálogo de resultados.
     * 
     * @param owner Ventana padre (modal)
     * @param title Título de la ventana
     * @param text Texto a mostrar en el área de resultados
     */
    public ResultView(JFrame owner, String title, String text) {
        super(owner, title, true);
        setSize(520, 360);
        setLocationRelativeTo(owner);
        JTextArea ta = new JTextArea(text);
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 14));
        ta.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(ta));
    }
}
