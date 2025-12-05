package view;

import util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Vista principal para la simulación de Movimiento Rectilíneo Uniforme (MRU).
 * Proporciona una interfaz gráfica con controles para configurar parámetros,
 * visualizar la animación y mostrar telemetría en tiempo real.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class MRUView extends JDialog {
    /** Formato para mostrar valores numéricos con 2 decimales */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    private final JTextField tfX0 = new JTextField("0");
    private final JTextField tfXf = new JTextField("");
    private final JTextField tfV = new JTextField("5");
    private final JTextField tfTime = new JTextField("5");

    private final JButton btnRun = new JButton("▶ Empezar");
    private final JButton btnTimeToX = new JButton("🧮 Calcular tiempo");
    private final JButton btnCalculateV = new JButton("⚡ Calcular velocidad");

    private final JLabel lblTimeValue = createValueLabel("0.00 s");
    private final JLabel lblPositionValue = createValueLabel("0.00 m");
    private final JLabel lblDistanceValue = createValueLabel("0.00 m");
    private final JLabel lblVelocityValue = createValueLabel("0.00 m/s");
    private final JLabel lblFinalPositionValue = createValueLabel("0.00 m");
    private final JLabel lblStatus = new JLabel("Listo para simular.");
    private final JProgressBar progressTime = new JProgressBar(0, 1000);

    private final MRUAnimationPanel panel = new MRUAnimationPanel();

    public MRUView(JFrame parent) {
        super(parent, "Movimiento Rectilíneo Uniforme", true);
        setSize(860, 540);
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(760, 480));

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(new Color(236, 242, 255));
        setContentPane(content);

        JPanel header = new JPanel(new BorderLayout(6, 6));
        header.setOpaque(false);
        JLabel title = new JLabel("Movimiento Rectilíneo Uniforme");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(30, 46, 97));
        JLabel subtitle = new JLabel("Explora cómo la velocidad constante define la posición y el tiempo.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(70, 90, 130));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        panel.setPreferredSize(new Dimension(520, 360));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 204, 231), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        content.add(panel, BorderLayout.CENTER);

        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        content.add(sidebar, BorderLayout.EAST);

        sidebar.add(createCard("Parámetros iniciales", buildFormPanel()));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(createCard("Telemetría", buildTelemetryPanel()));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(createCard("Controles", buildControlsPanel()));

        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatus.setForeground(new Color(40, 83, 150));
        progressTime.setStringPainted(true);
        progressTime.setForeground(new Color(102, 149, 255));
        progressTime.setBackground(new Color(225, 231, 246));

        getRootPane().setDefaultButton(btnRun);
        updateTelemetry(0, 0, 0, 0, 0, 0);
    }

    /**
     * Construye el panel de formulario con campos de entrada.
     * 
     * @return Panel con campos de posición inicial, final, velocidad y tiempo
     */
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createInputBlock("Posición inicial (m)", tfX0));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createInputBlock("Posición final (m)", tfXf));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createInputBlock("Velocidad (m/s)", tfV));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createInputBlock("Tiempo (s)", tfTime));
        return panel;
    }

    private JPanel buildTelemetryPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createTelemetryRow("Tiempo", lblTimeValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Posición", lblPositionValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Desplazamiento", lblDistanceValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Velocidad", lblVelocityValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Posición Final", lblFinalPositionValue));
        panel.add(Box.createVerticalStrut(12));
        panel.add(progressTime);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblStatus);

        return panel;
    }

    private JPanel buildControlsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        styleSecondaryButton(btnRun);
        styleSecondaryButton(btnTimeToX);
        styleSecondaryButton(btnCalculateV);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.add(btnRun);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(btnTimeToX);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(btnCalculateV);

        panel.add(buttons);

        return panel;
    }

    private JPanel createCard(String title, JComponent component) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(198, 211, 230), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(47, 66, 120));

        component.setOpaque(false);
        card.add(label, BorderLayout.NORTH);
        card.add(component, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInputBlock(String text, JTextField field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = createLabel(text);
        block.add(label);
        block.add(Box.createVerticalStrut(4));

        styleField(field);
        block.add(field);

        return block;
    }

    private JPanel createTelemetryRow(String title, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel label = createLabel(title);
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(60, 78, 120));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(20, 40, 92));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.BOLD, 14));
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setColumns(8);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(186, 202, 232), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(74, 117, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(240, 244, 255));
        button.setForeground(new Color(47, 66, 120));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Actualiza los valores de telemetría mostrados en la vista.
     * 
     * @param time Tiempo transcurrido en segundos
     * @param position Posición actual en metros
     * @param displacement Desplazamiento desde la posición inicial en metros
     * @param velocity Velocidad constante en m/s
     * @param finalPosition Posición final esperada en metros
     * @param progress Progreso de la simulación (0.0 a 1.0)
     */
    public void updateTelemetry(double time, double position, double displacement, double velocity, double finalPosition, double progress) {
        lblTimeValue.setText(DF.format(time) + " s");
        lblPositionValue.setText(DF.format(position) + " m");
        lblDistanceValue.setText(DF.format(displacement) + " m");
        String direction = velocity >= 0 ? "→ " : "← ";
        lblVelocityValue.setText(direction + DF.format(Math.abs(velocity)) + " m/s");
        lblFinalPositionValue.setText(DF.format(finalPosition) + " m");

        int progressValue = (int) Math.round(Math.max(0, Math.min(progress, 1)) * 1000);
        progressTime.setValue(progressValue);
        progressTime.setString(String.format("%d %% del tiempo", Math.round(progressValue / 10.0)));
    }

    /**
     * Muestra un mensaje de estado en la vista.
     * 
     * @param text Texto del estado a mostrar
     */
    public void showStatus(String text) {
        lblStatus.setText(text);
    }

    /**
     * Actualiza el estado de los botones según si la simulación está corriendo.
     * 
     * @param running true si la simulación está en progreso, false en caso contrario
     */
    public void setSimulationRunning(boolean running) {
        btnRun.setEnabled(!running);
        btnTimeToX.setEnabled(!running);
        btnCalculateV.setEnabled(!running);
        showStatus(running ? "Simulación en progreso…" : "Listo para simular.");
    }

    public String getX0() { return tfX0.getText(); }
    public String getXf() { return tfXf.getText(); }
    public String getV() { return tfV.getText(); }
    public String getTime() { return tfTime.getText(); }

    public JButton getBtnRun() { return btnRun; }
    public JButton getBtnTimeToX() { return btnTimeToX; }
    public JButton getBtnCalculateV() { return btnCalculateV; }
    
    public void setV(String value) { tfV.setText(value); }

    public double getPanelWidth() { return panel.getWidth(); }
    public void setCharacterX(double x) { panel.setX(x); }
    public void repaintPanel() { panel.repaint(); }
    public void resetPosition() { panel.setX(0); }

    /**
     * Panel interno que renderiza la animación visual del MRU.
     * Dibuja el escenario con cielo, carretera y el vehículo en movimiento.
     */
    static class MRUAnimationPanel extends JPanel {
        private double x = 0;
        private final ImageIcon car = ImageLoader.load("resources/car.png", 120, 70);

        public MRUAnimationPanel() {
            setBackground(new Color(222, 232, 255));
        }

        /**
         * Establece la posición horizontal del vehículo en píxeles.
         * 
         * @param x Posición X en píxeles (mínimo 20 para evitar salirse del borde)
         */
        public void setX(double x) {
            this.x = Math.max(20, x);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int horizon = height / 2;

            GradientPaint sky = new GradientPaint(0, 0, new Color(208, 228, 255), 0, horizon, new Color(144, 190, 255));
            g2.setPaint(sky);
            g2.fillRect(0, 0, width, horizon);

            GradientPaint ground = new GradientPaint(0, horizon, new Color(130, 198, 120), 0, height, new Color(85, 140, 90));
            g2.setPaint(ground);
            g2.fillRect(0, horizon, width, height - horizon);

            int roadY = horizon + 20;
            g2.setColor(new Color(70, 70, 70));
            g2.fillRoundRect(0, roadY, width, height - roadY + 20, 25, 25);

            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{22f, 22f}, 0));
            g2.setColor(new Color(255, 255, 255, 210));
            int lane = roadY + (height - roadY) / 2;
            g2.drawLine(30, lane, width - 30, lane);

            g2.setColor(new Color(255, 255, 255, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(12, 12, width - 24, height - 24, 40, 40);

            int carX = (int) Math.min(Math.max(x, 30), width - car.getIconWidth() - 30);
            car.paintIcon(this, g2, carX, lane - car.getIconHeight() + 5);

            g2.dispose();
        }
    }
}
