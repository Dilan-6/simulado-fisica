package controller;

import view.FreeFallView;
import view.MRUView;
import view.MainView;

public class MainController {
    private MainView view;

    public MainController(MainView view) {
        this.view = view;
        register();
    }

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
