package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * DashboardPanel: Versión Refinada.
 * Se eliminaron elementos innecesarios y se mejoró la alineación de
 * componentes.
 */
public class DashboardPanel extends JPanel {
    private JLabel lblTotalTrabajadores;
    private JLabel lblViajesPendientes;
    private JLabel lblTotalCapturas;
    private JButton btnActualizar;

    // Paleta Profesional (Slate & Blue)
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_TEXTO_PRIN = new Color(15, 23, 42);
    private final Color COLOR_TEXTO_SEC = new Color(100, 116, 139);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 40));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        initHeader();
        initCards();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Grupo de Texto (Título y Subtítulo)
        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel titleLabel = new JLabel("Panel de Control");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_TEXTO_PRIN);

        JLabel subtitleLabel = new JLabel("Resumen operativo del sistema FishGold.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(COLOR_TEXTO_SEC);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 5)));
        titleGroup.add(subtitleLabel);

        // Botón de actualización
        btnActualizar = createPrimaryButton("↺ Actualizar Datos");

        headerPanel.add(titleGroup, BorderLayout.WEST);
        headerPanel.add(btnActualizar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initCards() {
        // Grid de 1 fila y 3 columnas con espacio de 30px
        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsContainer.setOpaque(false);

        lblTotalTrabajadores = createModernCard(cardsContainer, "Trabajadores Activos", "👥", COLOR_PRIMARIO);
        lblViajesPendientes = createModernCard(cardsContainer, "Viajes en Curso", "🚢", new Color(245, 158, 11)); // Ámbar
        lblTotalCapturas = createModernCard(cardsContainer, "Capturas Registradas", "💰", new Color(16, 185, 129)); // Esmeralda

        add(cardsContainer, BorderLayout.CENTER);
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(COLOR_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        // Efecto Hover simple
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARIO.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARIO);
            }
        });

        return btn;
    }

    private JLabel createModernCard(JPanel parent, String title, String icon, Color accentColor) {
        // Panel personalizado con dibujo de tarjeta redondeada
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo de la tarjeta
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                // Línea de acento lateral (look moderno)
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 40, 4, 50, 4, 4);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 30, 25, 25));
        card.setOpaque(false);

        // Icono de la tarjeta
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 36));
        lblIcon.setForeground(accentColor);

        // Título de la métrica
        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(COLOR_TEXTO_SEC);

        // Valor numérico
        JLabel lblValue = new JLabel("0");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblValue.setForeground(COLOR_TEXTO_PRIN);

        // Contenedor de texto
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(lblTitle);
        content.add(lblValue);

        card.add(lblIcon, BorderLayout.NORTH);
        card.add(Box.createRigidArea(new Dimension(0, 15)), BorderLayout.CENTER);
        card.add(content, BorderLayout.SOUTH);

        parent.add(card);
        return lblValue;
    }

    // --- GETTERS ---
    public JLabel getLblTotalTrabajadores() {
        return lblTotalTrabajadores;
    }

    public JLabel getLblViajesPendientes() {
        return lblViajesPendientes;
    }

    public JLabel getLblTotalCapturas() {
        return lblTotalCapturas;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}