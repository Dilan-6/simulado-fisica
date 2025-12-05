package controller;

import javax.swing.*;
import model.MRUModel;
import view.MRUView;

/**
 * Controlador para la simulación de Movimiento Rectilíneo Uniforme (MRU).
 * Gestiona la lógica de negocio, la animación y la interacción entre
 * el modelo y la vista de MRU.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class MRUController {
    /** Vista asociada al controlador */
    private MRUView view;
    
    /** Timer que controla la animación de la simulación */
    private Timer animationTimer;
    
    /** Tiempo actual de la simulación en segundos */
    private double t;
    
    /** Modelo físico de MRU */
    private MRUModel model;
    
    /** Escala de conversión de metros a píxeles */
    private double pixelScale;
    
    /** Desplazamiento base en píxeles para centrar la animación */
    private double baseOffset;
    
    /** Tiempo total de la simulación en segundos */
    private double totalTime;
    
    /** Posición inicial del objeto en metros */
    private double x0;
    
    /** Posición final del objeto en metros */
    private double finalPosition;

    /**
     * Construye un nuevo controlador de MRU.
     * 
     * @param view Vista de MRU a controlar
     */
    public MRUController(MRUView view) {
        this.view = view;
        setup();
    }

    /**
     * Configura los listeners de los botones de la vista.
     */
    private void setup() {
        view.getBtnRun().addActionListener(e -> startAnimation());
        view.getBtnTimeToX().addActionListener(e -> onTimeToX());
        view.getBtnCalculateV().addActionListener(e -> onCalculateVelocity());
    }

    /**
     * Inicia la animación de MRU.
     * Lee los parámetros de la vista, calcula la velocidad si es necesario,
     * configura el modelo y comienza la animación.
     */
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

            // Determinar xEnd para escalar la animación
            double xEnd;
            if (xf != null) {
                xEnd = xf;
            } else {
                // Si no hay xf, usar la posición calculada al final del tiempo
                xEnd = model.positionAt(totalTime);
            }
            
            // Guardar la posición final para mostrarla en telemetría
            finalPosition = xEnd;

            view.resetPosition();
            view.updateTelemetry(0, model.positionAt(0), 0, v, finalPosition, 0);
            view.setSimulationRunning(true);
            view.showStatus("Simulación en progreso…");
            
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

    /**
     * Detiene la animación de MRU.
     * Cancela el timer de animación y actualiza el estado de la vista.
     */
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        view.setSimulationRunning(false);
    }

    /**
     * Actualiza el estado de la animación en cada frame.
     * Calcula la nueva posición, desplazamiento y telemetría, y actualiza la vista.
     * Si se alcanza el tiempo total, detiene la simulación.
     */
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
        view.updateTelemetry(t, x, displacement, model.getVelocity(), finalPosition, progress);
        view.showStatus(String.format("Avance: %.2f m", displacement));

        if (t >= totalTime) {
            stopAnimation();
            view.updateTelemetry(totalTime, model.positionAt(totalTime), model.displacementAt(totalTime), model.getVelocity(), finalPosition, 1);
            view.showStatus("Simulación completada.");
        }
    }

    /**
     * Calcula y muestra el tiempo necesario para llegar a una posición destino.
     * Valida que se proporcionen posición final y velocidad.
     */
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
            
            // Validar que la velocidad no sea cero
            if (v == 0) {
                JOptionPane.showMessageDialog(view, 
                    "La velocidad no puede ser cero para calcular el tiempo.\n" +
                    "Un objeto sin velocidad nunca llegará a un destino diferente.", 
                    "Velocidad inválida", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            MRUModel model = new MRUModel(x0, v);
            double t = model.timeToReach(xf);
            
            // Verificar si el resultado es válido
            if (Double.isInfinite(t) || t < 0) {
                String message;
                if (t < 0) {
                    message = String.format(
                        "No es posible llegar de x₀=%.2f m a xf=%.2f m con velocidad v=%.2f m/s.\n" +
                        "El objeto se está moviendo en dirección opuesta al destino.", 
                        x0, xf, v);
                } else {
                    message = "Error en el cálculo. Verifica los valores ingresados.";
                }
                JOptionPane.showMessageDialog(view, message, "Cálculo no válido", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, 
                    String.format("Tiempo para llegar de x₀=%.2f m a xf=%.2f m:\n%.2f s", x0, xf, t),
                    "Cálculo de tiempo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Calcula y muestra la velocidad necesaria para llegar a una posición final
     * en un tiempo dado. Actualiza el campo de velocidad en la vista.
     */
    private void onCalculateVelocity() {
        try {
            double x0 = Double.parseDouble(view.getX0());
            
            String xfText = view.getXf().trim();
            if (xfText.isEmpty()) {
                JOptionPane.showMessageDialog(view, 
                    "Por favor ingresa la posición final (xf) para calcular la velocidad.", 
                    "Falta posición final", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            double xf = Double.parseDouble(xfText);
            
            String timeText = view.getTime().trim();
            if (timeText.isEmpty()) {
                JOptionPane.showMessageDialog(view, 
                    "Por favor ingresa la duración (tiempo) para calcular la velocidad.", 
                    "Falta tiempo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            double t = Double.parseDouble(timeText);
            
            // Validar que el tiempo sea mayor que cero
            if (t <= 0) {
                JOptionPane.showMessageDialog(view, 
                    "El tiempo debe ser mayor que cero.", 
                    "Tiempo inválido", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Calcular velocidad: v = (xf - x0) / t
            double v = (xf - x0) / t;
            
            // Mostrar resultado y actualizar el campo
            view.setV(String.format("%.2f", v));
            
            JOptionPane.showMessageDialog(view, 
                String.format("Velocidad calculada:\nv = (xf - x₀) / t\nv = (%.2f - %.2f) / %.2f\nv = %.2f m/s", 
                    xf, x0, t, v),
                "Cálculo de velocidad",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Por favor ingresa valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
