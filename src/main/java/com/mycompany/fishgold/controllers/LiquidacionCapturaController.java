package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.LiquidacionCapturaPanel;
import com.mycompany.fishgold.util.Validator;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class LiquidacionCapturaController {
    private final LiquidacionCapturaPanel view;
    private final LiquidacionCapturaDAO dao;
    private final PlanificacionDAO planDAO;
    private final ConfiguracionDAO confDAO; // Requerido para el cálculo en tiempo real

    public LiquidacionCapturaController(LiquidacionCapturaPanel view, LiquidacionCapturaDAO dao,
            PlanificacionDAO planDAO) {
        this.view = view;
        this.dao = dao;
        this.planDAO = planDAO;
        this.confDAO = new ConfiguracionDAO();
        init();
    }

    private void init() {
        view.getBtnAdd().addActionListener(e -> registrarLiquidacion());
        view.getBtnUpdate().addActionListener(e -> actualizar());
        view.getBtnDelete().addActionListener(e -> eliminar());
        view.getBtnClear().addActionListener(e -> limpiarFormulario());

        // BUSQUEDA EN TIEMPO REAL
        view.getTxtSearch().getDocument().addDocumentListener(new SimpleDocumentListener(this::buscar));

        // LÓGICA DE EXPERTO: Cálculo en tiempo real al escribir el peso
        view.getTxtPesoPescado().getDocument()
                .addDocumentListener(new SimpleDocumentListener(this::calcularPrevisualizacion));

        // Al cambiar de viaje en el combo, también recalculamos (por si cambia la meta)
        view.getCbPlanificacion().addActionListener(e -> calcularPrevisualizacion());

        // Sincronización Tabla -> Formulario
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                seleccionarFila();
        });

        recargarVista();
    }

    private void calcularPrevisualizacion() {
        try {
            Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
            String pesoStr = view.getTxtPesoPescado().getText().trim();

            if (p == null || pesoStr.isEmpty() || !Validator.isDecimal(view.getTxtPesoPescado())) {
                view.getLblPreviewCalculo().setText("$ 0.00");
                return;
            }

            double pesoReal = Double.parseDouble(pesoStr);
            double meta = p.getMetaPesoKg();
            double precioBase = confDAO.getActual().getPagoKiloBase();
            double monto;

            if (pesoReal > meta) {
                double exceso = pesoReal - meta;
                monto = (meta * precioBase) + (exceso * (precioBase * 2));
            } else {
                monto = pesoReal * precioBase;
            }

            view.getLblPreviewCalculo().setText("$ " + String.format("%.2f", monto));

        } catch (Exception e) {
            view.getLblPreviewCalculo().setText("$ 0.00");
        }
    }

    private void registrarLiquidacion() {
        view.getErrPeso().setText(" ");
        view.getErrCapitan().setText(" ");

        Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(view, "⚠ Seleccione un viaje.");
            return;
        }

        if (!Validator.isDecimal(view.getTxtPesoPescado())) {
            view.getErrPeso().setText("⚠ Peso inválido");
            return;
        }

        if (!Validator.isNotBlank(view.getTxtCapitan())) {
            view.getErrCapitan().setText("⚠ Nombre requerido");
            return;
        }

        LiquidacionCaptura lc = new LiquidacionCaptura();
        lc.setPlanificacionId(p.getId());
        lc.setPesoTotalPescado(Double.parseDouble(view.getTxtPesoPescado().getText()));
        lc.setRegistradoPorCapitan(view.getTxtCapitan().getText());
        lc.setObservaciones(view.getTxtObservaciones().getText());

        if (dao.create(lc)) {
            JOptionPane.showMessageDialog(view, "✅ Liquidación procesada y viaje finalizado.");
            recargarVista();
        }
    }

    // --- MÉTODOS DE SOPORTE ---

    public void recargarVista() {
        cargarCombos();
        cargarTabla(dao.readAll());
        limpiarFormulario();
    }

    private void cargarCombos() {
        view.getCbPlanificacion().removeAllItems();
        planDAO.readAll().stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("En Curso") || p.getEstado().equalsIgnoreCase("Pendiente"))
                .forEach(view.getCbPlanificacion()::addItem);
    }

    private void cargarTabla(List<LiquidacionCaptura> lista) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        for (LiquidacionCaptura lc : lista) {
            model.addRow(new Object[] {
                    lc.getId(), lc.getPlanificacionCodigo(), lc.getPesoTotalPescado(),
                    "$" + String.format("%.2f", lc.getMontoFinalCalculado()),
                    lc.getRegistradoPorCapitan(), lc.getFechaCierre(), lc.getObservaciones()
            });
        }
    }

    private void buscar() {
        cargarTabla(dao.search(view.getTxtSearch().getText().trim()));
    }

    private void seleccionarFila() {
        int fila = view.getTable().getSelectedRow();
        if (fila != -1) {
            view.getTxtPesoPescado().setText(view.getTable().getValueAt(fila, 2).toString());
            view.getTxtCapitan().setText(view.getTable().getValueAt(fila, 4).toString());
            view.getTxtObservaciones().setText(view.getTable().getValueAt(fila, 6).toString());
        }
    }

    private void limpiarFormulario() {
        view.getTxtPesoPescado().setText("");
        view.getTxtCapitan().setText("");
        view.getTxtObservaciones().setText("");
        view.getLblPreviewCalculo().setText("$ 0.00");
        if (view.getCbPlanificacion().getItemCount() > 0)
            view.getCbPlanificacion().setSelectedIndex(0);
        view.getTable().clearSelection();
    }

    private void actualizar() {
        /* Lógica de actualización */ }

    private void eliminar() {
        /* Lógica de eliminación */ }

    private void generarReporte() {
        /* Lógica de PDF */ }

    // Clase interna para reducir código repetitivo de DocumentListener
    private class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        public SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            action.run();
        }
    }
}