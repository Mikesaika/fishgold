package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.MainView;

// Centraliza la lógica de navegación y distribuye las dependencias (DAOs).
public class MainController {
    private final MainView mainView;
    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();
    private final EmbarcacionDAO embarcacionDAO = new EmbarcacionDAO();
    private final FaenaDAO faenaDAO = new FaenaDAO();
    private final TripulacionDAO tripulacionDAO = new TripulacionDAO();
    private final CargoDAO cargoDAO = new CargoDAO();
    private DashboardController dashboardController;
    private TrabajadorController trabajadorController;
    private EmbarcacionController embarcacionController;
    private FaenaController faenaController;
    private TripulacionController tripulacionController;
    private AsistenciaController asistenciaController;

    public MainController(MainView mainView) {
        this.mainView = mainView;
        initializeSubControllers();
        setupListeners();
    }

    private void initializeSubControllers() {
        this.dashboardController = new DashboardController(
                mainView.getDashboardPanel(), faenaDAO, trabajadorDAO, embarcacionDAO);

        this.trabajadorController = new TrabajadorController(
                mainView.getTrabajadorPanel(), trabajadorDAO, cargoDAO);

        this.embarcacionController = new EmbarcacionController(
                mainView.getEmbarcacionPanel(), embarcacionDAO);

        this.faenaController = new FaenaController(
                mainView.getFaenaPanel(), faenaDAO, embarcacionDAO);

        this.tripulacionController = new TripulacionController(
                mainView.getTripulacionPanel(), tripulacionDAO, faenaDAO, trabajadorDAO, cargoDAO);

        this.asistenciaController = new AsistenciaController(
                mainView.getAsistenciaPanel(), tripulacionDAO, faenaDAO);
    }

    public void start() {
        mainView.setTitle("Fishgold - Sistema de Gestión Pesquera");
        mainView.setLocationRelativeTo(null);
        mainView.setVisible(true);
        showPanel("Dashboard");
    }

    private void setupListeners() {
        mainView.getBtnDashboard().addActionListener(e -> showPanel("Dashboard"));
        mainView.getBtnTrabajadores().addActionListener(e -> showPanel("Trabajadores"));
        mainView.getBtnEmbarcaciones().addActionListener(e -> showPanel("Embarcaciones"));
        mainView.getBtnTripulacion().addActionListener(e -> showPanel("Tripulacion")); // Nombre sin tilde para el //
                                                                                       // CardLayout
        mainView.getBtnFaenas().addActionListener(e -> showPanel("Faenas"));
        mainView.getBtnAsistencias().addActionListener(e -> showPanel("Asistencias"));
        mainView.getBtnLogout().addActionListener(e -> logout());
    }

    private void showPanel(String panelName) {
        if (panelName.equals("Dashboard"))
            dashboardController.actualizarDatos();
        if (panelName.equals("Faenas"))
            faenaController.cargarEmbarcaciones();

        mainView.getCardLayout().show(mainView.getContentPanel(), panelName);
    }

    private void logout() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                mainView, "¿Cerrar sesión?", "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            mainView.dispose();
        }
    }
}