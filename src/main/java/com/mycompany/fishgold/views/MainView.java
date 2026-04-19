package com.mycompany.fishgold.views;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JPanel sidebarPanel, contentPanel;
    private CardLayout cardLayout;

    // Paneles de contenido
    private DashboardPanel dashboardPanel;
    private TrabajadorPanel trabajadorPanel;
    private EmbarcacionPanel embarcacionPanel;
    private FaenaPanel faenaPanel;
    private TripulacionPanel tripulacionPanel;
    private AsistenciaPanel asistenciaPanel;
    // Botones de navegación
    private JButton btnDashboard, btnTrabajadores, btnEmbarcaciones, btnTripulacion, btnFaenas, btnAsistencias,
            btnLogout;
    // Colores corporativos
    private final Color COLOR_SIDEBAR = new Color(44, 62, 80);
    private final Color COLOR_HOVER = new Color(52, 152, 219);
    private final Color COLOR_ACTIVO = new Color(41, 128, 185);

    public MainView() {
        setTitle("Fishgold - Sistema de Gestión de Flota y Personal");
        setSize(1200, 800); // Espacio optimizado para resolución estándar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        // --- SIDEBAR (IZQUIERDA) ---
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        // Logo / Título
        JLabel logoLabel = new JLabel("FISHGOLD");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        sidebarPanel.add(logoLabel);
        // Creación de botones con diseño estilizado
        btnDashboard = createMenuButton("Dashboard");
        btnTrabajadores = createMenuButton("Trabajadores");
        btnEmbarcaciones = createMenuButton("Embarcaciones");
        btnTripulacion = createMenuButton("Tripulación");
        btnFaenas = createMenuButton("Faenas");
        btnAsistencias = createMenuButton("Asistencias");
        btnLogout = createMenuButton("Cerrar Sesión");
        btnLogout.setForeground(new Color(231, 76, 60));

        addMenuComponent(btnDashboard);
        addMenuComponent(btnTrabajadores);
        addMenuComponent(btnEmbarcaciones);
        addMenuComponent(btnTripulacion);
        addMenuComponent(btnFaenas);
        addMenuComponent(btnAsistencias);

        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        add(sidebarPanel, BorderLayout.WEST);

        // --- CONTENT AREA (CENTRO) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(236, 240, 241));

        // Inicialización de Paneles Reales
        dashboardPanel = new DashboardPanel();
        trabajadorPanel = new TrabajadorPanel();
        embarcacionPanel = new EmbarcacionPanel();
        faenaPanel = new FaenaPanel();
        tripulacionPanel = new TripulacionPanel();
        asistenciaPanel = new AsistenciaPanel();

        // Registro en CardLayout (Nombres de llave consistentes con el Controller)
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(trabajadorPanel, "Trabajadores");
        contentPanel.add(embarcacionPanel, "Embarcaciones");
        contentPanel.add(tripulacionPanel, "Tripulacion");
        contentPanel.add(faenaPanel, "Faenas");
        contentPanel.add(asistenciaPanel, "Asistencias");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void addMenuComponent(JButton btn) {
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setForeground(new Color(236, 240, 241));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_SIDEBAR);
            }
        });

        return btn;
    }

    // --- GETTERS PARA EL MAINCONTROLLER ---
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

    public TrabajadorPanel getTrabajadorPanel() {
        return trabajadorPanel;
    }

    public EmbarcacionPanel getEmbarcacionPanel() {
        return embarcacionPanel;
    }

    public FaenaPanel getFaenaPanel() {
        return faenaPanel;
    }

    public TripulacionPanel getTripulacionPanel() {
        return tripulacionPanel;
    }

    public AsistenciaPanel getAsistenciaPanel() {
        return asistenciaPanel;
    }

    public JButton getBtnDashboard() {
        return btnDashboard;
    }

    public JButton getBtnTrabajadores() {
        return btnTrabajadores;
    }

    public JButton getBtnEmbarcaciones() {
        return btnEmbarcaciones;
    }

    public JButton getBtnTripulacion() {
        return btnTripulacion;
    }

    public JButton getBtnFaenas() {
        return btnFaenas;
    }

    public JButton getBtnAsistencias() {
        return btnAsistencias;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }
}