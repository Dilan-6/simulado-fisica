package simuladorfisica;

import controller.MainController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.MainView;

public class Main {


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            new MainController(mainView);
            mainView.setVisible(true);
        });
    }
}
