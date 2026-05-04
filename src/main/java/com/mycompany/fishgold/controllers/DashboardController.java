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

    public DashboardController(DashboardPanel view, TrabajadorDAO trabajadorDAO,
            PlanificacionDAO planificacionDAO, LiquidacionCapturaDAO liquidacionDAO) {
        this.view = view;
        this.trabajadorDAO = trabajadorDAO;
        this.planificacionDAO = planificacionDAO;
        this.liquidacionDAO = liquidacionDAO;
        init();
    }

    private void init() {
        actualizarDatos();
        view.getBtnActualizar().addActionListener(e -> actualizarDatos());
    }

    public void actualizarDatos() {
        setLabelsText("...");
        view.getBtnActualizar().setEnabled(false);
        new DashboardWorker().execute();
    }

    // Usamos Object[] para poder mezclar Integers y Strings (para el dinero
    // formateado)
    private class DashboardWorker extends SwingWorker<Object[], Void> {
        @Override
        protected Object[] doInBackground() throws Exception {
            // 1. Personal Activo
            int activos = trabajadorDAO.countByEstado("Activo");

            // 2. Operatividad: Viajes que están ocupando recursos
            int operativos = planificacionDAO.countByEstado("Pendiente") +
                    planificacionDAO.countByEstado("En Curso");

            // 3. LÓGICA DE EXPERTO: Total de dinero producido por las capturas
            // Sumamos todos los montos finales calculados automáticamente
            double ingresoTotal = liquidacionDAO.readAll().stream()
                    .mapToDouble(LiquidacionCaptura::getMontoFinalCalculado)
                    .sum();

            return new Object[] { activos, operativos, ingresoTotal };
        }

        @Override
        protected void done() {
            try {
                Object[] datos = get();

                // Actualización de la UI con los datos reales
                view.getLblTotalTrabajadores().setText(String.valueOf(datos[0]));
                view.getLblViajesPendientes().setText(String.valueOf(datos[1]));

                // Formateo de moneda para la tarjeta de ingresos
                double total = (double) datos[2];
                view.getLblTotalCapturas().setText("$" + String.format("%.0f", total));

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
    }
}