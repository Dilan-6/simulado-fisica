package controller;
import view.FreeFallView;
import view.MRUView;
import view.MainView;

/**
 * Controlador principal de la aplicación.
 * Gestiona la navegación entre las diferentes vistas de simulación
 * (Caída Libre y MRU) desde la vista principal.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class MainController {
    /** Vista principal de la aplicación */
    private MainView view;

    /**
     * Construye un nuevo controlador principal.
     * 
     * @param view Vista principal de la aplicación
     */
    public MainController(MainView view) {
        this.view = view;
        register();
    }

    /**
     * Registra los listeners de los botones de la vista principal.
     * Cada botón abre su respectiva ventana de simulación.
     */
    private void register() {
        view.getBtnFreeFall().addActionListener(e -> {
            FreeFallView ffView = new FreeFallView(view);
            new FreeFallController(ffView);
            ffView.setVisible(true);
        });

        view.getBtnMRU().addActionListener(e -> {
            MRUView mruView = new MRUView(view);
            new MRUController(mruView);
            mruView.setVisible(true);
        });
    }
}
