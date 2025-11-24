package controller;

import javax.swing.*;
import model.MRUModel;
import view.MRUView;

public class MRUController {
    private MRUView view;
    private Timer animationTimer;
    private double t;
    private MRUModel model;
    private double pixelScale;
    private double baseOffset;
    private double totalTime;
    private double x0;

    public MRUController(MRUView view) {
        this.view = view;
        setup();
    }

    private void setup() {
        view.getBtnRun().addActionListener(e -> startAnimation());
        view.getBtnTimeToX().addActionListener(e -> onTimeToX());
    }

    private void startAnimation() {
        try {
            x0 = Double.parseDouble(view.getX0());
            totalTime = Double.parseDouble(view.getTime());

            if (totalTime <= 0) {
                JOptionPane.showMessageDialog(view, "El tiempo debe ser mayor a cero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener xf si está disponible
            String xfText = view.getXf().trim();
            Double xf = null;
            if (!xfText.isEmpty()) {
                xf = Double.parseDouble(xfText);
            }

            // Calcular velocidad: si v está vacío o es 0, y hay xf, calcular con v = (xf - x0) / t
            double v;
            String vText = view.getV().trim();
            boolean calcularVelocidad = vText.isEmpty();
            
            // Si no está vacío, verificar si es 0
            if (!calcularVelocidad) {
                try {
                    double vInput = Double.parseDouble(vText);
                    if (vInput == 0 && xf != null) {
                        calcularVelocidad = true;
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número válido, se manejará después
                }
            }
            
            if (calcularVelocidad) {
                if (xf == null) {
                    JOptionPane.showMessageDialog(view, 
                        "Para calcular la velocidad automáticamente, ingresa la posición final (xf).", 
                        "Faltan datos", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                v = (xf - x0) / totalTime;
            } else {
                v = Double.parseDouble(vText);
            }

            model = new MRUModel(x0, v);

            stopAnimation();

            view.resetPosition();
            view.updateTelemetry(0, model.positionAt(0), 0, v, 0);
            view.setSimulationRunning(true);
            view.showStatus("Simulación en progreso…");

            // Determinar xEnd para escalar la animación
            double xEnd;
            if (xf != null) {
                xEnd = xf;
            } else {
                // Si no hay xf, usar la posición calculada al final del tiempo
                xEnd = model.positionAt(totalTime);
            }
            
            double minX = Math.min(x0, xEnd);
            double maxX = Math.max(x0, xEnd);
            double range = maxX - minX;

            double panelWidth = view.getPanelWidth();
            if (panelWidth <= 0) panelWidth = 600;

            double margin = 90;
            if (range < 1e-3) {
                pixelScale = 0;
                baseOffset = panelWidth / 2.0 - 60;
            } else {
                double availableWidth = Math.max(panelWidth - margin * 2, 200);
                pixelScale = availableWidth / range;
                baseOffset = margin - (minX * pixelScale);
            }

            t = 0;

            animationTimer = new Timer(40, e -> update());
            animationTimer.start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        view.setSimulationRunning(false);
    }

    private void update() {
        t += 0.05;
        if (t > totalTime) t = totalTime;

        double x = model.positionAt(t);
        double displacement = model.displacementAt(t);

        double xPx = baseOffset + x * pixelScale;
        if (Double.isNaN(xPx) || Double.isInfinite(xPx)) {
            xPx = view.getPanelWidth() / 2.0;
        }

        view.setCharacterX(xPx);
        view.repaintPanel();

        double progress = Math.min(1, Math.max(0, t / totalTime));
        view.updateTelemetry(t, x, displacement, model.getVelocity(), progress);
        view.showStatus(String.format("Avance: %.2f m", displacement));

        if (t >= totalTime) {
            stopAnimation();
            view.updateTelemetry(totalTime, model.positionAt(totalTime), model.displacementAt(totalTime), model.getVelocity(), 1);
            view.showStatus("Simulación completada.");
        }
    }

    private void onTimeToX() {
        try {
            double x0 = Double.parseDouble(view.getX0());
            
            String xfText = view.getXf().trim();
            if (xfText.isEmpty()) {
                JOptionPane.showMessageDialog(view, 
                    "Por favor ingresa la posición final (xf) para calcular el tiempo.", 
                    "Falta posición final", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            double xf = Double.parseDouble(xfText);
            
            String vText = view.getV().trim();
            if (vText.isEmpty()) {
                JOptionPane.showMessageDialog(view, 
                    "Por favor ingresa un valor de velocidad para calcular el tiempo.", 
                    "Falta velocidad", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double v = Double.parseDouble(vText);
            MRUModel model = new MRUModel(x0, v);
            double t = model.timeToReach(xf);
            JOptionPane.showMessageDialog(view, 
                String.format("Tiempo para llegar de x₀=%.2f m a xf=%.2f m:\n%.2f s", x0, xf, t),
                "Cálculo de tiempo",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
