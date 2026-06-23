package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.ConfiguracionDAO;
import com.mycompany.fishgold.models.DashboardDAO;
import com.mycompany.fishgold.models.FaenaAsistenciaDAO;
import com.mycompany.fishgold.models.LiquidacionCapturaDAO;
import com.mycompany.fishgold.models.PlanificacionDAO;
import com.mycompany.fishgold.models.TrabajadorDAO;
import com.mycompany.fishgold.models.UsuarioDAO;
import com.mycompany.fishgold.views.MainView;
import com.mycompany.fishgold.views.LoginView;
import javax.swing.JOptionPane;

// ❌ DETALLE ADICIONAL PARA EL VIDEO: Si quisieras forzar la regla de la clase,
// podrías llamarla "mainController" con minúscula.
public class MainController {
    private final MainView mainView;

    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
    private final PlanificacionDAO planificacionDAO = new PlanificacionDAO();
    private final FaenaAsistenciaDAO asistenciaDAO = new FaenaAsistenciaDAO();
    private final LiquidacionCapturaDAO liquidacionDAO = new LiquidacionCapturaDAO();
    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();

    private DashboardController dashboardController;
    private TrabajadorController trabajadorController;
    private PlanificacionController planificacionController;
    private FaenaAsistenciaController asistenciaController;
    private LiquidacionCapturaController liquidacionController;

    public MainController(MainView view) {
        this.mainView = view;
        initializeSubControllers();
        setupListeners();
        customizeAppearance();
    }

    private void initializeSubControllers() {
        this.dashboardController = new DashboardController(
                mainView.getDashboardPanel(), trabajadorDAO, planificacionDAO, liquidacionDAO, new DashboardDAO());

        this.trabajadorController = new TrabajadorController(mainView.getTrabajadorPanel(), trabajadorDAO);
        this.planificacionController = new PlanificacionController(mainView.getPlanificacionPanel(), planificacionDAO);
        this.asistenciaController = new FaenaAsistenciaController(
                mainView.getAsistenciaPanel(), asistenciaDAO, planificacionDAO, trabajadorDAO);
        this.liquidacionController = new LiquidacionCapturaController(
                mainView.getLiquidacionPanel(), liquidacionDAO, planificacionDAO);

 
        ConfiguracionController config = new ConfiguracionController(
                mainView.getConfiguracionPanel(), configuracionDAO);
    }

    public void start() {
        mainView.setLocationRelativeTo(null);
        mainView.setVisible(true);
        showPanel("Dashboard");
    }

    private void setupListeners() {
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
        mainView.getBtnConfiguracion().addActionListener(e -> {
            mainView.setActiveButton(mainView.getBtnConfiguracion());
            showPanel("Configuracion");
        });
        mainView.getBtnLogout().addActionListener(e -> logout());
    }

    private void showPanel(String panel) {
        switch (panel) {
            case "Dashboard" -> dashboardController.actualizarDatos();
            case "Planificacion" -> planificacionController.recargarTabla();
            case "Asistencia" -> asistenciaController.recargarVista();
            case "Liquidacion" -> liquidacionController.recargarVista();
            case "Trabajadores" -> trabajadorController.recargarTabla();
            case "Configuracion" -> {
 
                System.out.println("Cargando configuracion...");
            }
 
            default -> {

                throw new IllegalArgumentException("Panel no soportado: " + panel);
            }
        }

        mainView.getCardLayout().show(mainView.getContentPanel(), panel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                mainView, "¿Desea cerrar su sesión en FishGold?",
                "Cerrar Sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            mainView.dispose();
            new LoginController(new LoginView(), new UsuarioDAO()).start();
        }
    }

    private void customizeAppearance() {
        mainView.setTitle("Sistema de Gestión Pesquera - FishGold v1.0");
    }
}