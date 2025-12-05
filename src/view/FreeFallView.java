package view;

import util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Vista principal para la simulación de caída libre.
 * Proporciona una interfaz gráfica con controles para configurar parámetros,
 * visualizar la animación y mostrar telemetría en tiempo real.
 * 
 * @author SimuladorFisica
 * @version 1.0
 */
public class FreeFallView extends JDialog {
    /** Formato para mostrar valores numéricos con 2 decimales */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    /** Campo de texto para la altura inicial en metros */
    private final JTextField tfHeight = new JTextField("50");
    
    /** Campo de texto para la velocidad inicial en m/s */
    private final JTextField tfV0 = new JTextField("0");
    
    /** Botón para iniciar la simulación */
    private final JButton btnSimular = new JButton("▶ Empezar");
    
    /** Botón para detener la simulación */
    private final JButton btnDetener = new JButton("⏸ Detener");
    
    /** Botón para calcular el tiempo hasta el suelo */
    private final JButton btnCalcTimeGround = new JButton("🧮 Tiempo al suelo");

    /** Radio button para seleccionar la pelota como objeto */
    private final JRadioButton rbBall = new JRadioButton("Pelota", true);
    
    /** Radio button para seleccionar el dinosaurio como objeto */
    private final JRadioButton rbDino = new JRadioButton("Dino");
    
    /** Panel que muestra la animación de la caída */
    private final AnimationPanel animationPanel = new AnimationPanel();

    /** Etiqueta que muestra el tiempo transcurrido */
    private final JLabel lblTimeValue = createValueLabel("0.00 s");
    
    /** Etiqueta que muestra la altura actual */
    private final JLabel lblHeightValue = createValueLabel("0.00 m");
    
    /** Etiqueta que muestra la distancia caída */
    private final JLabel lblDistanceValue = createValueLabel("0.00 m");
    
    /** Etiqueta que muestra la velocidad actual */
    private final JLabel lblVelocityValue = createValueLabel("0.00 m/s");
    
    /** Etiqueta que muestra el tiempo restante hasta el suelo */
    private final JLabel lblRemainingValue = createValueLabel("0.00 s");
    
    /** Etiqueta que muestra el estado de la simulación */
    private final JLabel lblStatus = new JLabel("Listo para simular.");
    
    /** Barra de progreso que muestra el avance del tiempo */
    private final JProgressBar progressTime = new JProgressBar(0, 1000);

    /**
     * Construye una nueva vista de caída libre.
     * 
     * @param parent Ventana padre (modal)
     */
    public FreeFallView(JFrame parent) {
        super(parent, "Caída Libre", true);
        setSize(880, 640);
        setMinimumSize(new Dimension(780, 540));
        setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(235, 243, 255));
        setContentPane(content);

        JPanel header = new JPanel(new BorderLayout(6, 6));
        header.setOpaque(false);
        JLabel title = new JLabel("Caída Libre");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(28, 55, 107));
        JLabel subtitle = new JLabel("Observa el efecto de la gravedad durante la caída de objetos.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(74, 95, 135));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        animationPanel.setPreferredSize(new Dimension(520, 420));
        animationPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 204, 231), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        content.add(animationPanel, BorderLayout.CENTER);

        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 0));
        content.add(sidebar, BorderLayout.EAST);

        sidebar.add(createCard("Parámetros iniciales", buildInputPanel()));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(createCard("Seleccionar objeto", buildObjectPanel()));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(createCard("Telemetría", buildTelemetryPanel()));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(createCard("Controles", buildControlsPanel()));

        ButtonGroup group = new ButtonGroup();
        group.add(rbBall);
        group.add(rbDino);

        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatus.setForeground(new Color(30, 80, 140));
        progressTime.setStringPainted(true);
        progressTime.setForeground(new Color(255, 148, 107));
        progressTime.setBackground(new Color(255, 228, 216));

        btnDetener.setEnabled(false);
        updateTelemetry(255, 255, 255, 255, -1, 255);
        getRootPane().setDefaultButton(btnSimular);
    }

    /**
     * Construye el panel de entrada de parámetros iniciales.
     * 
     * @return Panel con campos de altura y velocidad inicial
     */
    private JPanel buildInputPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createInputBlock("Altura inicial (m)", tfHeight));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createInputBlock("Velocidad inicial (m/s)", tfV0));
        return panel;
    }

    /**
     * Construye el panel de selección de objeto.
     * 
     * @return Panel con opciones de pelota o dinosaurio
     */
    private JPanel buildObjectPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Icon ballIcon = ImageLoader.load("resources/ball.png", 36, 36);
        Icon dinoIcon = ImageLoader.load("resources/dino_parachute.png", 40, 40);

        styleRadio(rbBall);
        styleRadio(rbDino);

        panel.add(createRadioRow(rbBall, ballIcon));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createRadioRow(rbDino, dinoIcon));
        return panel;
    }

    /**
     * Construye el panel de telemetría con valores en tiempo real.
     * 
     * @return Panel con etiquetas de tiempo, altura, distancia, velocidad y progreso
     */
    private JPanel buildTelemetryPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createTelemetryRow("Tiempo", lblTimeValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Altura", lblHeightValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Distancia caída", lblDistanceValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Velocidad", lblVelocityValue));
        panel.add(Box.createVerticalStrut(6));
        panel.add(createTelemetryRow("Tiempo restante", lblRemainingValue));
        panel.add(Box.createVerticalStrut(12));
        panel.add(progressTime);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblStatus);

        return panel;
    }

    /**
     * Construye el panel de controles con botones de simulación.
     * 
     * @return Panel con botones de empezar, detener y calcular tiempo
     */
    private JPanel buildControlsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        styleSecondaryButton(btnSimular, new Color(255, 120, 76));
        styleSecondaryButton(btnDetener, new Color(255, 200, 184));
        styleGhostButton(btnCalcTimeGround);

        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.add(btnSimular);
        row.add(btnDetener);

        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnCalcTimeGround);

        return panel;
    }

    /**
     * Crea un panel con estilo de tarjeta (card) con título y contenido.
     * 
     * @param title Título de la tarjeta
     * @param component Componente a colocar dentro de la tarjeta
     * @return Panel con estilo de tarjeta
     */
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
        label.setForeground(new Color(40, 64, 120));

        component.setOpaque(false);
        card.add(label, BorderLayout.NORTH);
        card.add(component, BorderLayout.CENTER);

        return card;
    }

    /**
     * Crea un bloque de entrada con etiqueta y campo de texto.
     * 
     * @param title Texto de la etiqueta
     * @param field Campo de texto a incluir
     * @return Panel con el bloque de entrada
     */
    private JPanel createInputBlock(String title, JTextField field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = createLabel(title);
        block.add(label);
        block.add(Box.createVerticalStrut(4));

        styleField(field);
        block.add(field);

        return block;
    }

    /**
     * Crea una fila de telemetría con etiqueta y valor.
     * 
     * @param title Texto de la etiqueta
     * @param value Etiqueta que muestra el valor
     * @return Panel con la fila de telemetría
     */
    private JPanel createTelemetryRow(String title, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel label = createLabel(title);
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    /**
     * Crea una etiqueta con estilo estándar.
     * 
     * @param text Texto de la etiqueta
     * @return JLabel con estilo aplicado
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(63, 81, 129));
        return label;
    }

    /**
     * Crea una etiqueta para mostrar valores numéricos con estilo destacado.
     * 
     * @param text Texto inicial del valor
     * @return JLabel con estilo de valor aplicado
     */
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(24, 45, 92));
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

    private JComponent createRadioRow(JRadioButton radio, Icon icon) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(radio, BorderLayout.WEST);
        JLabel preview = new JLabel(icon);
        row.add(preview, BorderLayout.EAST);
        return row;
    }

    private void styleRadio(JRadioButton radio) {
        radio.setFont(new Font("SansSerif", Font.BOLD, 13));
        radio.setForeground(new Color(47, 66, 120));
        radio.setFocusPainted(false);
        radio.setOpaque(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(new Color(107, 47, 35));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleGhostButton(JButton button) {
        button.setBackground(new Color(247, 250, 255));
        button.setForeground(new Color(47, 66, 120));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Actualiza los valores de telemetría mostrados en la vista.
     * 
     * @param time Tiempo transcurrido en segundos
     * @param height Altura actual en metros
     * @param distanceFallen Distancia caída en metros
     * @param velocity Velocidad actual en m/s (positiva hacia abajo)
     * @param timeRemaining Tiempo restante hasta el suelo en segundos (-1 si no aplica)
     * @param progress Progreso de la simulación (0.0 a 1.0)
     */
    public void updateTelemetry(double time, double height, double distanceFallen, double velocity, double timeRemaining, double progress) {
        lblTimeValue.setText(DF.format(time) + " s");
        lblHeightValue.setText(DF.format(height) + " m");
        lblDistanceValue.setText(DF.format(distanceFallen) + " m");

        boolean downward = velocity >= 0;
        String arrow = downward ? "↓ " : "↑ ";
        lblVelocityValue.setText(arrow + DF.format(Math.abs(velocity)) + " m/s");

        if (timeRemaining < 0) {
            lblRemainingValue.setText("—");
        } else {
            lblRemainingValue.setText(DF.format(timeRemaining) + " s");
        }

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
        btnSimular.setEnabled(!running);
        btnDetener.setEnabled(running);
        btnCalcTimeGround.setEnabled(!running);
        showStatus(running ? "Simulación en progreso…" : "Listo para simular.");
    }

    public String getHeightString() { return tfHeight.getText(); }
    public String getV0() { return tfV0.getText(); }
    public boolean isBallSelected() { return rbBall.isSelected(); }

    public JButton getBtnSimular() { return btnSimular; }
    public JButton getBtnDetener() { return btnDetener; }
    public JButton getBtnCalcTimeGround() { return btnCalcTimeGround; }
    public AnimationPanel getAnimationPanel() { return animationPanel; }

    /**
     * Panel interno que renderiza la animación visual de la caída libre.
     * Dibuja el escenario con cielo, nubes, edificios, suelo y el objeto en caída.
     */
    public static class AnimationPanel extends JPanel {
        private ImageIcon icon = ImageLoader.load("resources/ball.png", 80, 80);
        private double y = 50;

        public AnimationPanel() {
            setBackground(new Color(223, 236, 255));
        }

        /**
         * Establece el icono del objeto a animar.
         * 
         * @param icon Icono del objeto (pelota o dinosaurio)
         */
        public void setCharacter(ImageIcon icon) {
            this.icon = icon;
            repaint();
        }

        /**
         * Establece la posición vertical del objeto en píxeles.
         * 
         * @param y Posición Y en píxeles
         */
        public void setY(double y) {
            this.y = y;
            repaint();
        }

        /**
         * Mueve el objeto verticalmente por un desplazamiento dado.
         * Usado para el efecto de rebote al tocar el suelo.
         * 
         * @param offset Desplazamiento en píxeles (positivo hacia abajo)
         */
        public void move(double offset) {
            this.y += offset;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int ground = height - 100;

            GradientPaint sky = new GradientPaint(0, 0, new Color(183, 216, 255), 0, height, new Color(236, 248, 255));
            g2.setPaint(sky);
            g2.fillRect(0, 0, width, height);

            g2.setColor(new Color(255, 255, 255, 160));
            g2.fillOval(60, 60, 160, 80);
            g2.fillOval(240, 40, 180, 90);
            g2.fillOval(width - 240, 70, 190, 90);

            g2.setColor(new Color(157, 188, 218));
            g2.fillRoundRect(60, ground - 240, 120, 240, 26, 26);
            g2.fillRoundRect(width - 150, ground - 200, 90, 200, 22, 22);

            g2.setColor(new Color(118, 172, 108));
            g2.fillRoundRect(0, ground, width, 140, 40, 40);

            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(18, 18, width - 36, height - 36, 40, 40);

            drawScale(g2, height, ground);

            int iconX = width / 2 - icon.getIconWidth() / 2;
            int iconY = (int) Math.min(y, ground - icon.getIconHeight());
            icon.paintIcon(this, g2, iconX, iconY);

            g2.dispose();
        }

        private void drawScale(Graphics2D g2, int height, int ground) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(255, 255, 255, 200));
            int scaleX = 40;
            g2.drawLine(scaleX, 40, scaleX, ground);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(255, 255, 255, 220));

            int segments = 6;
            int segmentHeight = (ground - 40) / segments;
            for (int i = 0; i <= segments; i++) {
                int y = ground - i * segmentHeight;
                g2.drawLine(scaleX - 6, y, scaleX + 6, y);
                g2.drawString(i * 10 + " m", scaleX + 12, y + 4);
            }
        }
    }
}
