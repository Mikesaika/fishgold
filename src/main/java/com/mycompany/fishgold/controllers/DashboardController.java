package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.DashboardPanel;
import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

public class DashboardController {
    private final DashboardPanel view;
    private final TrabajadorDAO trabajadorDAO;
    private final PlanificacionDAO planificacionDAO;
    private final LiquidacionCapturaDAO liquidacionDAO;
    private final DashboardDAO dashboardDAO;

    public DashboardController(DashboardPanel view, TrabajadorDAO trabajadorDAO,
            PlanificacionDAO planificacionDAO, LiquidacionCapturaDAO liquidacionDAO,
            DashboardDAO dashboardDAO) {
        this.view = view;
        this.trabajadorDAO = trabajadorDAO;
        this.planificacionDAO = planificacionDAO;
        this.liquidacionDAO = liquidacionDAO;
        this.dashboardDAO = dashboardDAO;
        init();
    }

    private void init() {
        actualizarDatos();
        view.getBtnActualizar().addActionListener(e -> actualizarDatos());
    }

    public void actualizarDatos() {
        setLabelsText("…");
        view.getBtnActualizar().setEnabled(false);
        new DashboardWorker().execute();
    }

    private class DashboardWorker extends SwingWorker<Object[], Void> {
        @Override
        protected Object[] doInBackground() {
            int activos = trabajadorDAO.countByEstado("Activo");
            int operativos = planificacionDAO.countByEstado("Pendiente") +
                    planificacionDAO.countByEstado("En Curso");
            double ingresoTotal = liquidacionDAO.readAll().stream()
                    .mapToDouble(LiquidacionCaptura::getMontoFinalCalculado)
                    .sum();
            double kilosSemana = dashboardDAO.kilosTotalesSemana();
            int totalLiq = dashboardDAO.countLiquidacionesTotales();
            int[] asist7 = dashboardDAO.conteoAsistenciasUltimos7Dias();
            return new Object[] { activos, operativos, ingresoTotal, kilosSemana, totalLiq, asist7 };
        }

        @Override
        protected void done() {
            try {
                Object[] datos = get();
                view.getLblTotalTrabajadores().setText(String.valueOf(datos[0]));
                view.getLblViajesPendientes().setText(String.valueOf(datos[1]));
                double total = (double) datos[2];
                view.getLblTotalCapturas().setText("$" + String.format("%.0f", total));
                double kg = (double) datos[3];
                view.getLblKilosSemana().setText(String.format("%.1f kg", kg));
                view.getLblTotalLiquidaciones().setText(String.valueOf(datos[4]));
                view.setDatosAsistenciaChart((int[]) datos[5]);
            } catch (InterruptedException | ExecutionException e) {
                setLabelsText("Error");
            } finally {
                view.getBtnActualizar().setEnabled(true);
            }
        }
    }

    private void setLabelsText(String text) {
        view.getLblTotalTrabajadores().setText(text);
        view.getLblViajesPendientes().setText(text);
        view.getLblTotalCapturas().setText(text);
        view.getLblKilosSemana().setText(text);
        view.getLblTotalLiquidaciones().setText(text);
    }
}
