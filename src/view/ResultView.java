package view;

import javax.swing.*;
import java.awt.*;

public class ResultView extends JDialog {
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
