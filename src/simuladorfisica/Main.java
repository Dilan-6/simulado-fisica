package simuladorfisica;

import controller.MainController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.MainView;

/**
 * Clase principal de la aplicación Simulador de Física.
 * Inicializa la interfaz gráfica y configura el Look and Feel del sistema.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class Main {

    /**
     * Punto de entrada principal de la aplicación.
     * Configura el Look and Feel del sistema y crea la vista principal
     * en el hilo de eventos de Swing (EDT).
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
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
