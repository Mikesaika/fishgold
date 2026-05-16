package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.LiquidacionCapturaPanel;
import com.mycompany.fishgold.util.Validator;
import com.mycompany.fishgold.util.LiquidacionCalculo;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class LiquidacionCapturaController {
    private final LiquidacionCapturaPanel view;
    private final LiquidacionCapturaDAO dao;
    private final PlanificacionDAO planDAO;
    private final ConfiguracionDAO confDAO;

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
        view.getBtnEliminar().addActionListener(e -> eliminarLiquidacionSeleccionada());

        view.getTxtSearch().getDocument().addDocumentListener(new SimpleDocumentListener(this::buscar));
        view.getTxtPesoPescado().getDocument()
                .addDocumentListener(new SimpleDocumentListener(this::calcularPrevisualizacion));
        view.getCbPlanificacion().addActionListener(e -> calcularPrevisualizacion());

        recargarVista();
    }

    private void calcularPrevisualizacion() {
        try {
            Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
            if (p == null) {
                view.getLblPreviewCalculo().setText("$ 0.00");
                return;
            }
            String pesoStr = view.getTxtPesoPescado().getText().trim();
            if (pesoStr.isEmpty() || !Validator.isDecimal(view.getTxtPesoPescado())) {
                view.getLblPreviewCalculo().setText("$ 0.00");
                return;
            }
            double pesoReal = Double.parseDouble(pesoStr);
            double meta = p.getMetaPesoKg();
            double precioBase = confDAO.getActual().getPagoKiloBase();
            double monto = LiquidacionCalculo.calcularMonto(pesoReal, meta, precioBase);
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
            JOptionPane.showMessageDialog(view, "Seleccione un viaje.");
            return;
        }

        if (!Validator.isDecimal(view.getTxtPesoPescado())) {
            view.getErrPeso().setText("Peso inválido");
            return;
        }

        if (!Validator.isNotBlank(view.getTxtCapitan())) {
            view.getErrCapitan().setText("Nombre requerido");
            return;
        }

        LiquidacionCaptura lc = new LiquidacionCaptura();
        lc.setPlanificacionId(p.getId());
        lc.setPesoTotalPescado(Double.parseDouble(view.getTxtPesoPescado().getText()));
        lc.setRegistradoPorCapitan(view.getTxtCapitan().getText());
        lc.setObservaciones(view.getTxtObservaciones().getText());

        if (dao.create(lc)) {
            JOptionPane.showMessageDialog(view, "Liquidación registrada y viaje finalizado.");
            recargarVista();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo registrar. ¿El viaje ya tiene liquidación?",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLiquidacionSeleccionada() {
        int fila = view.getTable().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(view, "Seleccione una fila en la tabla de liquidaciones.");
            return;
        }
        int id = (int) view.getTable().getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(view,
                "Se eliminará esta liquidación y el viaje volverá a estado Pendiente para poder registrar una nueva captura.\n¿Continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.deleteById(id)) {
            JOptionPane.showMessageDialog(view, "Liquidación eliminada. Puede crear una nueva desde este módulo.");
            recargarVista();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar la liquidación.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recargarVista() {
        cargarCombos();
        cargarTabla(dao.readAll());
        limpiarFormulario();
    }

    private void cargarCombos() {
        view.getCbPlanificacion().removeAllItems();
        planDAO.readAll().stream()
                .filter(p -> p.isActivo())
                .filter(x -> "En Curso".equalsIgnoreCase(x.getEstado()) || "Pendiente".equalsIgnoreCase(x.getEstado()))
                .filter(p -> planDAO.countLiquidaciones(p.getId()) == 0)
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

    private void limpiarFormulario() {
        view.getTxtPesoPescado().setText("");
        view.getTxtCapitan().setText("");
        view.getTxtObservaciones().setText("");
        view.getLblPreviewCalculo().setText("$ 0.00");
        if (view.getCbPlanificacion().getItemCount() > 0) {
            view.getCbPlanificacion().setSelectedIndex(0);
        }
        view.getTable().clearSelection();
    }

    private class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        SimpleDocumentListener(Runnable action) {
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
