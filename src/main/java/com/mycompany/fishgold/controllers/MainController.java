package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.MainView;
import com.mycompany.fishgold.views.LoginView;
import javax.swing.JOptionPane;

/**
 * MainController: Orquestador principal del sistema FishGold.
 * Gestiona el ciclo de vida de los sub-controladores y la navegación
 * CardLayout.
 */
public class MainController {
    private final MainView mainView;

    // Instancias únicas de DAOs para compartir conexión (Eficiencia)
    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
    private final PlanificacionDAO planificacionDAO = new PlanificacionDAO();
    private final FaenaAsistenciaDAO asistenciaDAO = new FaenaAsistenciaDAO();
    private final LiquidacionCapturaDAO liquidacionDAO = new LiquidacionCapturaDAO();
    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO(); // Nuevo DAO

    // Sub-controladores
    private DashboardController dashboardController;
    private TrabajadorController trabajadorController;
    private PlanificacionController planificacionController;
    private FaenaAsistenciaController asistenciaController;
    private LiquidacionCapturaController liquidacionController;
    private ConfiguracionController configuracionController; // Nuevo Controlador

    public MainController(MainView mainView) {
        this.mainView = mainView;
        initializeSubControllers();
        setupListeners();
        customizeAppearance();
    }

    private void initializeSubControllers() {
        // Inyectamos las dependencias necesarias a cada controlador hijo
        this.dashboardController = new DashboardController(
                mainView.getDashboardPanel(), trabajadorDAO, planificacionDAO, liquidacionDAO);

        this.trabajadorController = new TrabajadorController(
                mainView.getTrabajadorPanel(), trabajadorDAO);

        this.planificacionController = new PlanificacionController(
                mainView.getPlanificacionPanel(), planificacionDAO);

        this.asistenciaController = new FaenaAsistenciaController(
                mainView.getAsistenciaPanel(), asistenciaDAO, planificacionDAO, trabajadorDAO);

        this.liquidacionController = new LiquidacionCapturaController(
                mainView.getLiquidacionPanel(), liquidacionDAO, planificacionDAO);

        // Inicialización del nuevo controlador de configuración
        this.configuracionController = new ConfiguracionController(
                mainView.getConfiguracionPanel(), configuracionDAO);
    }

    public void start() {
        mainView.setLocationRelativeTo(null);
        mainView.setVisible(true);
        showPanel("Dashboard");
    }

    private void setupListeners() {
        // Navegación Sidebar con actualización de estado visual
        mainView.getBtnDashboard().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnDashboard());
            showPanel("Dashboard");
        });

        mainView.getBtnTrabajadores().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnTrabajadores());
            showPanel("Trabajadores");
        });

        mainView.getBtnPlanificacion().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnPlanificacion());
            showPanel("Planificacion");
        });

        mainView.getBtnAsistencia().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnAsistencia());
            showPanel("Asistencia");
        });

        mainView.getBtnLiquidacion().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnLiquidacion());
            showPanel("Liquidacion");
        });

        // Listener para el nuevo botón de configuración
        mainView.getBtnConfiguracion().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnConfiguracion());
            showPanel("Configuracion");
        });

        // Acción de salida segura
        mainView.getBtnLogout().addActionListener(e -> logout());
    }

    private void showPanel(String panelName) {
        // REFRESH DINÁMICO: Antes de mostrar, actualizamos los datos de la BD
        switch (panelName) {
            case "Dashboard" -> dashboardController.actualizarDatos();
            case "Planificacion" -> planificacionController.recargarTabla();
            case "Asistencia" -> asistenciaController.recargarVista();
            case "Liquidacion" -> liquidacionController.recargarVista();
            case "Trabajadores" -> trabajadorController.recargarTabla();
            // La configuración no suele requerir refresh dinámico pero se puede añadir si
            // es necesario
        }

        // Cambio visual de panel
        mainView.getCardLayout().show(mainView.getContentPanel(), panelName);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                mainView, "¿Desea cerrar su sesión en FishGold?",
                "Cerrar Sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            mainView.dispose();

            // Reiniciar flujo de acceso (Login)
            LoginView loginView = new LoginView();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            LoginController loginController = new LoginController(loginView, usuarioDAO);
            loginController.start();
        }
    }

    private void customizeAppearance() {
        mainView.setTitle("Sistema de Gestión Pesquera - FishGold v1.0");
    }
}