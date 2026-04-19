package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.DashboardPanel;
import javax.swing.SwingWorker;

//Controlador para el Dashboard.
public class DashboardController {
    private final DashboardPanel view;
    private final FaenaDAO faenaDAO;
    private final TrabajadorDAO trabajadorDAO;
    private final EmbarcacionDAO embarcacionDAO;

    public DashboardController(DashboardPanel view, FaenaDAO faenaDAO, TrabajadorDAO trabajadorDAO,
            EmbarcacionDAO embarcacionDAO) {
        this.view = view;
        this.faenaDAO = faenaDAO;
        this.trabajadorDAO = trabajadorDAO;
        this.embarcacionDAO = embarcacionDAO;
        init();
    }

    private void init() {
        actualizarDatos();
        view.getBtnActualizar().addActionListener(e -> actualizarDatos());
    }

    // Usa SwingWorker para que la UI no se congele si la base de datos tarda.

    public void actualizarDatos() {
        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                // Obtenemos los conteos directamente desde el DAO (con SQL COUNT)
                int pendientes = faenaDAO.countByEstado("Pendiente");
                int activos = trabajadorDAO.countByEstado("Activo");
                int embarcaciones = embarcacionDAO.countByEstado("Activa");
                return new int[] { pendientes, activos, embarcaciones };
            }

            @Override
            protected void done() {
                try {
                    int[] resultados = get();
                    view.getLblFaenasPendientes().setText(String.valueOf(resultados[0]));
                    view.getLblTotalTrabajadores().setText(String.valueOf(resultados[1]));
                    view.getLblTotalEmbarcaciones().setText(String.valueOf(resultados[2]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}