package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de Dashboard con tarjetas de métricas visuales.
 */
public class DashboardPanel extends JPanel {
    private JLabel lblFaenasPendientes;
    private JLabel lblTotalTrabajadores;
    private JLabel lblTotalEmbarcaciones;
    private JButton btnActualizar;

    // Colores de la paleta profesional
    private final Color COLOR_PRIMARIO = new Color(41, 128, 185); // Azul
    private final Color COLOR_FONDO = new Color(236, 240, 241); // Gris muy claro
    private final Color COLOR_TEXTO = new Color(44, 62, 80); // Gris oscuro

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        initComponents();
    }

    private void initComponents() {
        // Título del Dashboard
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(COLOR_FONDO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 0));

        JLabel title = new JLabel("Panel de Control Fishgold");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(COLOR_TEXTO);
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Contenedor de Tarjetas
        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 25, 0));
        cardsContainer.setBackground(COLOR_FONDO);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Creación de tarjetas con colores temáticos
        lblFaenasPendientes = createMetricCard(cardsContainer, "Faenas Pendientes", "0", new Color(231, 76, 60)); // Rojo
        lblTotalTrabajadores = createMetricCard(cardsContainer, "Trabajadores Activos", "0", new Color(46, 204, 113)); // Verde
        lblTotalEmbarcaciones = createMetricCard(cardsContainer, "Flota Activa", "0", COLOR_PRIMARIO);

        add(cardsContainer, BorderLayout.CENTER);

        // Barra inferior con botón estilizado
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(COLOR_FONDO);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 40));

        btnActualizar = new JButton("Refrescar Estadísticas");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(btnActualizar);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createMetricCard(JPanel parent, String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(0, 5));
        card.add(accentBar, BorderLayout.NORTH);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(127, 140, 141));
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 54));
        lblValue.setForeground(COLOR_TEXTO);
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(lblTitle);
        infoPanel.add(lblValue);

        card.add(infoPanel, BorderLayout.CENTER);

        parent.add(card);
        return lblValue;
    }

    public JLabel getLblFaenasPendientes() {
        return lblFaenasPendientes;
    }

    public JLabel getLblTotalTrabajadores() {
        return lblTotalTrabajadores;
    }

    public JLabel getLblTotalEmbarcaciones() {
        return lblTotalEmbarcaciones;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}