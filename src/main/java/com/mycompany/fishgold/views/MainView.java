package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainView: Versión Final Corregida.
 * Incluye la nueva sección de Configuración y mejoras en la navegación lateral.
 */
public class MainView extends JFrame {
    private JPanel sidebarPanel, contentPanel;
    private CardLayout cardLayout;

    // Paneles de contenido
    private DashboardPanel dashboardPanel;
    private TrabajadorPanel trabajadorPanel;
    private PlanificacionPanel planificacionPanel;
    private FaenaAsistenciaPanel asistenciaPanel;
    private LiquidacionCapturaPanel liquidacionPanel;
    private ConfiguracionPanel configuracionPanel; // Nueva Vista

    // Navegación
    private JButton btnDashboard, btnTrabajadores, btnPlanificacion, btnAsistencia, btnLiquidacion, btnConfiguracion,
            btnLogout;
    private JButton currentButton;

    // Paleta Profesional
    private final Color COLOR_SIDEBAR = new Color(15, 23, 42);
    private final Color COLOR_HOVER = new Color(30, 41, 59);
    private final Color COLOR_ACTIVO = new Color(37, 99, 235);
    private final Color COLOR_TEXT_OFF = new Color(148, 163, 184);

    public MainView() {
        setTitle("FishGold — Gestión pesquera");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(280, 0));

        // Logo Section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 45));
        logoPanel.setOpaque(false);
        JLabel lblLogo = new JLabel("FISHGOLD");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        logoPanel.add(lblLogo);

        // Menú de Botones
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 15, 0, 15));

        // Instanciación de botones con íconos
        btnDashboard = createMenuButton("Panel de Control");
        btnTrabajadores = createMenuButton("Personal");
        btnPlanificacion = createMenuButton("Planificación");
        btnAsistencia = createMenuButton("Faena / Asistencia");
        btnLiquidacion = createMenuButton("Liquidación");
        btnConfiguracion = createMenuButton("Configuración");

        // Adición al panel con espaciado rígido
        menuPanel.add(btnDashboard);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(btnTrabajadores);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(btnPlanificacion);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(btnAsistencia);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(btnLiquidacion);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        menuPanel.add(btnConfiguracion);

        // Footer (Perfil y Logout)
        JPanel footerPanel = new JPanel(new BorderLayout(0, 15));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(0, 20, 30, 20));

        JLabel lblUser = new JLabel(
                "<html><b>Miguel Núñez</b><br><font color='#94a3b8'>Administrador del Sistema</font></html>");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnLogout = new JButton("Cerrar Sesión");
        styleLogoutButton(btnLogout);

        footerPanel.add(lblUser, BorderLayout.NORTH);
        footerPanel.add(btnLogout, BorderLayout.SOUTH);

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(menuPanel, BorderLayout.CENTER);
        sidebarPanel.add(footerPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);

        // --- CONTENT AREA ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        // Inicialización de paneles corregidos
        dashboardPanel = new DashboardPanel();
        trabajadorPanel = new TrabajadorPanel();
        planificacionPanel = new PlanificacionPanel();
        asistenciaPanel = new FaenaAsistenciaPanel();
        liquidacionPanel = new LiquidacionCapturaPanel();
        configuracionPanel = new ConfiguracionPanel(); // Nuevo Panel

        // Registro en CardLayout
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(trabajadorPanel, "Trabajadores");
        contentPanel.add(planificacionPanel, "Planificacion");
        contentPanel.add(asistenciaPanel, "Asistencia");
        contentPanel.add(liquidacionPanel, "Liquidacion");
        contentPanel.add(configuracionPanel, "Configuracion");

        add(contentPanel, BorderLayout.CENTER);

        setActiveButton(btnDashboard);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton("  " + text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(COLOR_TEXT_OFF);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 25, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != currentButton) {
                    btn.setBackground(COLOR_HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (btn != currentButton) {
                    btn.setBackground(COLOR_SIDEBAR);
                    btn.setForeground(COLOR_TEXT_OFF);
                }
            }
        });
        return btn;
    }

    public void setActiveButton(JButton btn) {
        if (currentButton != null) {
            currentButton.setBackground(COLOR_SIDEBAR);
            currentButton.setForeground(COLOR_TEXT_OFF);
        }
        currentButton = btn;
        currentButton.setBackground(COLOR_ACTIVO);
        currentButton.setForeground(Color.WHITE);
    }

    private void styleLogoutButton(JButton btn) {
        btn.setBackground(new Color(239, 68, 68));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));
    }

    // --- GETTERS ACTUALIZADOS ---
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

    public PlanificacionPanel getPlanificacionPanel() {
        return planificacionPanel;
    }

    public FaenaAsistenciaPanel getAsistenciaPanel() {
        return asistenciaPanel;
    }

    public LiquidacionCapturaPanel getLiquidacionPanel() {
        return liquidacionPanel;
    }

    public ConfiguracionPanel getConfiguracionPanel() {
        return configuracionPanel;
    }

    public JButton getBtnDashboard() {
        return btnDashboard;
    }

    public JButton getBtnTrabajadores() {
        return btnTrabajadores;
    }

    public JButton getBtnPlanificacion() {
        return btnPlanificacion;
    }

    public JButton getBtnAsistencia() {
        return btnAsistencia;
    }

    public JButton getBtnLiquidacion() {
        return btnLiquidacion;
    }

    public JButton getBtnConfiguracion() {
        return btnConfiguracion;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }
}