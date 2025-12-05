package controller;
import model.FreeFallModel;
import util.ImageLoader;
import view.FreeFallView;

import javax.swing.*;

/**
 * Controlador para la simulación de caída libre.
 * Gestiona la lógica de negocio, la animación y la interacción entre
 * el modelo y la vista de caída libre.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class FreeFallController {
    /** Vista asociada al controlador */
    private final FreeFallView view;
    
    /** Timer que controla la animación de la simulación */
    private Timer animationTimer;
    
    /** Tiempo actual de la simulación en segundos */
    private double t;
    
    /** Modelo físico de caída libre */
    private FreeFallModel model;

    /** Nivel del suelo en píxeles dentro del panel de animación */
    private double groundLevel;
    
    /** Escala de conversión de metros a píxeles */
    private double pixelScale;
    
    /** Tiempo total hasta que el objeto toca el suelo */
    private double totalTime;
    
    /** Altura inicial del objeto en metros */
    private double initialHeight;
    
    /** Velocidad inicial del objeto en m/s */
    private double initialVelocity;
    
    /** Aceleración gravitacional */
    private double acceleration;

    /**
     * Construye un nuevo controlador de caída libre.
     * 
     * @param view Vista de caída libre a controlar
     */
    public FreeFallController(FreeFallView view) {
        this.view = view;
        setup();
    }

    /**
     * Configura los listeners de los botones de la vista.
     */
    private void setup() {
        view.getBtnSimular().addActionListener(e -> startSimulation());
        view.getBtnDetener().addActionListener(e -> stopSimulation());
        view.getBtnCalcTimeGround().addActionListener(e -> calcTimeGround());
    }

    /**
     * Inicia la simulación de caída libre.
     * Lee los parámetros de la vista, valida los datos, configura el modelo
     * y comienza la animación.
     */
    private void startSimulation() {
        try {
            initialHeight = Double.parseDouble(view.getHeightString());
            initialVelocity = Double.parseDouble(view.getV0());
            acceleration = FreeFallModel.G;

            if (initialHeight < 0) {
                JOptionPane.showMessageDialog(view, "La altura debe ser un valor positivo.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            model = new FreeFallModel(initialHeight, initialVelocity, acceleration);

            // Cambiar imagen según selección del usuario
            if (view.isBallSelected()) {
                view.getAnimationPanel().setCharacter(ImageLoader.load("resources/ball.png", 90, 90));
            } else {
                view.getAnimationPanel().setCharacter(ImageLoader.load("resources/dino_parachute.png", 110, 110));
            }

            stopSimulation();

            double panelHeight = view.getAnimationPanel().getHeight();
            if (panelHeight <= 0) {
                panelHeight = view.getAnimationPanel().getPreferredSize().height;
            }
            if (panelHeight <= 0) panelHeight = 420;

            groundLevel = panelHeight - 120;
            double maxHeight = Math.max(initialHeight, 1);
            pixelScale = (groundLevel - 60) / maxHeight;

            t = 0;
            totalTime = model.timeToGround();

            view.setSimulationRunning(true);
            view.updateTelemetry(0, initialHeight, 0, initialVelocity, totalTime, 0);
            view.showStatus("Simulación en progreso…");

            double initialYpx = groundLevel - (initialHeight * pixelScale);
            view.getAnimationPanel().setY(initialYpx);

            animationTimer = new Timer(25, e -> updateAnimation());
            animationTimer.start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Detiene la simulación de caída libre.
     * Cancela el timer de animación y actualiza el estado de la vista.
     */
    private void stopSimulation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        view.setSimulationRunning(false);
    }

    /**
     * Actualiza el estado de la animación en cada frame.
     * Calcula la nueva posición, velocidad y telemetría, y actualiza la vista.
     * Si el objeto llega al suelo, detiene la simulación e inicia el efecto de rebote.
     */
    private void updateAnimation() {
        if (model == null) return;

        t += 0.05;
        double y = model.positionAt(t);

        double height = Math.max(y, 0);
        double distanceFallen = Math.max(initialHeight - height, 0);
        double velocity = model.velocityAt(t);
        double timeRemaining = totalTime < 0 ? -1 : Math.max(totalTime - t, 0);
        double progress = totalTime > 0 ? Math.min(t / totalTime, 1) : 0;

        view.updateTelemetry(t, height, distanceFallen, velocity, timeRemaining, progress);
        view.showStatus(String.format("Altura actual: %.2f m", height));

        if (y <= 0) {
            stopSimulation();
            view.getAnimationPanel().setY(groundLevel - 10);
            view.getAnimationPanel().repaint();

            view.updateTelemetry(totalTime > 0 ? totalTime : t, 0, initialHeight, model.velocityAt(totalTime > 0 ? totalTime : t), 0, 1);
            view.showStatus("Impacto completado.");

            // rebote
            new Timer(20, new java.awt.event.ActionListener() {
                int count = 0;
                boolean up = true;
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (count++ > 15) ((Timer) e.getSource()).stop();
                    double offset = up ? -3 : 3;
                    view.getAnimationPanel().move(offset);
                    if (count % 3 == 0) up = !up;
                }
            }).start();

            return;
        }

        double yPx = groundLevel - (y * pixelScale);
        view.getAnimationPanel().setY(yPx);
        view.getAnimationPanel().repaint();
    }

    /**
     * Calcula y muestra el tiempo necesario para que el objeto toque el suelo.
     * También muestra la velocidad de impacto.
     */
    private void calcTimeGround() {
        try {
            double y0 = Double.parseDouble(view.getHeightString());
            double v0 = Double.parseDouble(view.getV0());
            double a = FreeFallModel.G;

            FreeFallModel model = new FreeFallModel(y0, v0, a);
            double tGround = model.timeToGround();

            JOptionPane.showMessageDialog(view,
                    tGround >= 0 ? String.format("Tiempo hasta el suelo: %.2f s\nVelocidad de impacto: %.2f m/s", tGround, Math.abs(model.velocityAt(tGround)))
                                 : "El objeto no toca el suelo.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
