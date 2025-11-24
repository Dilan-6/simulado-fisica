package view;

import util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView extends JFrame {
    private final JButton btnFreeFall = createPrimaryButton("Explorar caída libre");
    private final JButton btnMRU = createPrimaryButton("Explorar MRU");

    public MainView() {
        super("Simulador de Física");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout(24, 24));
        background.setBorder(new EmptyBorder(36, 40, 30, 40));
        setContentPane(background);

        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("Simulador de Movimiento");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Anima experimentos de Caída Libre y MRU con telemetría en vivo.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(230, 238, 255));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        background.add(header, BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel(new GridLayout(1, 2, 24, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.add(createSimulationCard(
                "Caída Libre", "Analiza el efecto de la gravedad durante la caída.",
                new String[]{"Altura configurable", "Velocidad inicial", "Animación con rebote", "Datos de impacto"},
                ImageLoader.load("resources/ball.png", 120, 120),
                btnFreeFall,
                new Color(255, 149, 118)));

        cardsContainer.add(createSimulationCard(
                "Movimiento Rectilíneo Uniforme", "Visualiza desplazamiento y velocidad constante.",
                new String[]{"Escala ajustable", "Tiempo objetivo", "Telemetría en tiempo real", "Trayectoria suavizada"},
                ImageLoader.load("resources/car.png", 140, 84),
                btnMRU,
                new Color(110, 167, 255)));

        background.add(cardsContainer, BorderLayout.CENTER);

        JLabel footer = new JLabel("Selecciona un escenario para comenzar a experimentar.", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        footer.setForeground(new Color(216, 228, 255));
        background.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createSimulationCard(String title, String description, String[] bullets, Icon icon, JButton actionButton, Color accent) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout(18, 18));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 80), 1, true),
                new EmptyBorder(24, 26, 24, 26)
        ));

        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setOpaque(false);
        card.add(overlay, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(230, 236, 255));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.add(titleLabel);
        textBlock.add(Box.createVerticalStrut(6));
        textBlock.add(descriptionLabel);
        textBlock.add(Box.createVerticalStrut(12));
        textBlock.add(createBulletList(bullets));
        textBlock.add(Box.createVerticalGlue());

        overlay.add(textBlock, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(iconLabel);
        right.add(Box.createVerticalStrut(20));
        stylePrimaryCTA(actionButton, accent);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(actionButton);
        overlay.add(right, BorderLayout.EAST);

        makeCardClickable(card, actionButton);

        return card;
    }

    private JPanel createBulletList(String[] bullets) {
        JPanel bulletPanel = new JPanel();
        bulletPanel.setOpaque(false);
        bulletPanel.setLayout(new BoxLayout(bulletPanel, BoxLayout.Y_AXIS));
        Font font = new Font("SansSerif", Font.PLAIN, 13);
        for (String bullet : bullets) {
            JLabel label = new JLabel("• " + bullet);
            label.setFont(font);
            label.setForeground(new Color(210, 225, 255));
            bulletPanel.add(label);
            bulletPanel.add(Box.createVerticalStrut(4));
        }
        return bulletPanel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void stylePrimaryCTA(JButton button, Color accent) {
        button.setBackground(accent);
        button.setBorder(new EmptyBorder(12, 22, 12, 22));
    }

    public JButton getBtnFreeFall() { return btnFreeFall; }
    public JButton getBtnMRU() { return btnMRU; }

    private void makeCardClickable(JPanel card, JButton actionButton) {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actionButton.doClick();
            }
        };

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addRecursiveMouseListener(card, actionButton, adapter);
    }

    private void addRecursiveMouseListener(Component component, JButton actionButton, MouseAdapter adapter) {
        if (component == actionButton) return;
        component.addMouseListener(adapter);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addRecursiveMouseListener(child, actionButton, adapter);
            }
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(47, 73, 140), getWidth(), getHeight(), new Color(24, 33, 62));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
