package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.PlanificacionPanel;
import com.mycompany.fishgold.util.Validator;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlanificacionController {
    private final PlanificacionPanel view;
    private final PlanificacionDAO dao;
    private final TrabajadorDAO trabDAO;

    /** Última lista mostrada en checkboxes (misma referencia que en la vista). */
    private List<Trabajador> ultimaListaTripulacion = List.of();

    public PlanificacionController(PlanificacionPanel view, PlanificacionDAO dao) {
        this.view = view;
        this.dao = dao;
        this.trabDAO = new TrabajadorDAO();
        init();
    }

    private void init() {
        view.getBtnAdd().addActionListener(e -> agregar());
        view.getBtnUpdate().addActionListener(e -> actualizar());
        view.getBtnDelete().addActionListener(e -> eliminar());
        view.getBtnClear().addActionListener(e -> limpiarFormulario());

        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar();
            }
        });

        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        recargarTabla();
    }

    public void recargarTabla() {
        cargarTabla(dao.readAll());
        recargarTripulacionChecks(null);
    }

    private void recargarTripulacionChecks(Integer planIdEdicion) {
        ultimaListaTripulacion = trabDAO.readDisponiblesParaPlanificacion(planIdEdicion);
        Set<Integer> seleccionados = new HashSet<>();
        if (planIdEdicion != null && planIdEdicion > 0) {
            seleccionados = trabDAO.readByPlanificacion(planIdEdicion).stream()
                    .map(Trabajador::getId)
                    .collect(Collectors.toSet());
        }
        view.rebuildTripulacionChecks(ultimaListaTripulacion, seleccionados);
        view.setAyudaTripulacion("Marque a quienes viajan. Un trabajador ya asignado a un viaje sin liquidar no aparece aquí.");
    }

    private List<Trabajador> tripulacionSeleccionadaActual() {
        return view.getTrabajadoresSeleccionados(ultimaListaTripulacion);
    }

    private void cargarTabla(List<Planificacion> lista) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        for (Planificacion p : lista) {
            model.addRow(new Object[] {
                    p.getId(), p.getCodigoViaje(), p.getEmbarcacionNombre(),
                    p.getDestinoRuta(), p.getFechaSalidaProgramada(),
                    p.getMetaPesoKg(), p.getEstado()
            });
        }
    }

    private void agregar() {
        Planificacion p = extraerDatosDeVista(false, null);
        List<Trabajador> seleccionados = tripulacionSeleccionadaActual();

        if (p == null) {
            return;
        }

        if (seleccionados.isEmpty()) {
            view.getErrTripulacion().setText("Seleccione al menos un tripulante (casilla).");
            return;
        }

        for (Trabajador t : seleccionados) {
            if (trabDAO.estaOcupadoSinLiquidar(t.getId(), null)) {
                JOptionPane.showMessageDialog(view,
                        t.getNombreCompleto() + " ya está asignado a una planificación activa sin liquidar.\n"
                                + "Espere a liquidar ese viaje o quite al tripulante de la otra planificación.",
                        "Tripulante no disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (dao.createConTripulacion(p, seleccionados)) {
            JOptionPane.showMessageDialog(view, "Viaje y tripulación registrados correctamente.");
            limpiarFormulario();
            recargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Error al registrar: código duplicado u otro problema en base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione una planificación de la tabla.");
            return;
        }

        java.util.Date origfecha = null;
        Object fechaval = view.getTable().getValueAt(fila, 4);
        if (fechaval instanceof Date) {
            origfecha = new java.util.Date(((Date) fechaval).getTime());
        }

        Planificacion p = extraerDatosDeVista(true, origfecha);
        if (p == null) {
            return;
        }

        int planId = (int) view.getTable().getValueAt(fila, 0);
        p.setId(planId);

        List<Trabajador> nuevaTripulacion = tripulacionSeleccionadaActual();
        if (nuevaTripulacion.isEmpty()) {
            view.getErrTripulacion().setText("Seleccione al menos un tripulante (casilla).");
            return;
        }

        for (Trabajador t : nuevaTripulacion) {
            if (trabDAO.estaOcupadoSinLiquidar(t.getId(), planId)) {
                JOptionPane.showMessageDialog(view,
                        t.getNombreCompleto() + " ya está en otro viaje sin liquidar.",
                        "Tripulante no disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        boolean exito = dao.updateConTripulacion(p, nuevaTripulacion);

        if (exito) {
            JOptionPane.showMessageDialog(view, "Planificación actualizada correctamente.");
            limpiarFormulario();
            recargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Fallo al actualizar los datos en la base de datos.");
        }
    }

    private void eliminar() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            return;
        }

        int id = (int) view.getTable().getValueAt(fila, 0);
        String codigo = view.getTable().getValueAt(fila, 1).toString();

        if (dao.tieneDependencias(id)) {
            JOptionPane.showMessageDialog(view,
                    "No se puede desactivar la planificación porque tiene asistencias o liquidaciones asociadas.\n"
                            + "Cámbiela a estado «Cancelada» desde el formulario.",
                    "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Desactivar el viaje " + codigo + "?\nNo se borrará de la base de datos; dejará de mostrarse en la lista.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.softDelete(id)) {
                JOptionPane.showMessageDialog(view, "Planificación desactivada correctamente.");
                limpiarFormulario();
                recargarTabla();
            } else {
                JOptionPane.showMessageDialog(view, "No se pudo desactivar el registro.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscar() {
        cargarTabla(dao.search(view.getTxtSearch().getText().trim()));
    }

    private void limpiarFormulario() {
        view.getTxtCodigo().setText("");
        view.getTxtEmbarcacion().setText("");
        view.getTxtDestino().setText("");
        view.getTxtMetaPeso().setText("");
        view.getSpinFechaSalida().setValue(new java.util.Date());
        view.getCbEstado().setSelectedIndex(0);
        view.getTable().clearSelection();

        view.getErrCodigo().setText(" ");
        view.getErrEmbarcacion().setText(" ");
        view.getErrDestino().setText(" ");
        view.getErrMeta().setText(" ");
        view.getErrFecha().setText(" ");
        view.getErrTripulacion().setText(" ");

        Validator.applyStyle(view.getTxtCodigo(), true);
        Validator.applyStyle(view.getTxtEmbarcacion(), true);
        Validator.applyStyle(view.getTxtDestino(), true);
        Validator.applyStyle(view.getTxtMetaPeso(), true);
        Validator.applyStyle(view.getSpinFechaSalida(), true);

        recargarTripulacionChecks(null);
    }

    private void seleccionarFila() {
        int fila = view.getTable().getSelectedRow();
        if (fila != -1) {
            view.getTxtCodigo().setText(view.getTable().getValueAt(fila, 1).toString());
            view.getTxtEmbarcacion().setText(view.getTable().getValueAt(fila, 2).toString());
            view.getTxtDestino().setText(view.getTable().getValueAt(fila, 3).toString());

            Object fecha = view.getTable().getValueAt(fila, 4);
            if (fecha instanceof Date) {
                view.getSpinFechaSalida().setValue(new java.util.Date(((Date) fecha).getTime()));
            }

            view.getTxtMetaPeso().setText(view.getTable().getValueAt(fila, 5).toString());
            view.getCbEstado().setSelectedItem(view.getTable().getValueAt(fila, 6).toString());

            int planId = (int) view.getTable().getValueAt(fila, 0);
            recargarTripulacionChecks(planId);
        }
    }

    private Planificacion extraerDatosDeVista(boolean isupdate, java.util.Date orig) {
        view.getErrCodigo().setText(" ");
        view.getErrEmbarcacion().setText(" ");
        view.getErrDestino().setText(" ");
        view.getErrMeta().setText(" ");
        view.getErrFecha().setText(" ");

        boolean v1 = Validator.isNotBlank(view.getTxtCodigo());
        boolean v2 = Validator.isNotBlank(view.getTxtEmbarcacion());
        boolean v3 = Validator.isDecimal(view.getTxtMetaPeso());
        boolean v5 = Validator.isNotBlank(view.getTxtDestino());

        java.util.Date date = null;
        boolean v4 = true;
        String errmsg = " ";

        try {
            view.getSpinFechaSalida().commitEdit();
            date = (java.util.Date) view.getSpinFechaSalida().getValue();
        } catch (java.text.ParseException e) {
            v4 = false;
            errmsg = "Fecha requerida";
        }

        if (v4) {
            if (view.getSpinFechaSalida().getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor editor = (JSpinner.DateEditor) view.getSpinFechaSalida().getEditor();
                String text = editor.getTextField().getText();
                if (text == null || text.trim().isEmpty()) {
                    v4 = false;
                    errmsg = "Fecha requerida";
                }
            }
        }

        if (v4 && date == null) {
            v4 = false;
            errmsg = "Fecha requerida";
        }

        if (v4) {
            java.time.LocalDate sel = new Date(date.getTime()).toLocalDate();
            java.time.LocalDate today = java.time.LocalDate.now();
            if (isupdate && orig != null) {
                java.time.LocalDate origld = new Date(orig.getTime()).toLocalDate();
                if (!sel.equals(origld)) {
                    if (sel.isBefore(today)) {
                        v4 = false;
                        errmsg = "La fecha debe ser actual o futura";
                    }
                }
            } else {
                if (sel.isBefore(today)) {
                    v4 = false;
                    errmsg = "La fecha debe ser actual o futura";
                }
            }
        }

        Validator.applyStyle(view.getSpinFechaSalida(), v4);

        if (!v1) {
            view.getErrCodigo().setText("Código requerido");
        }
        if (!v2) {
            view.getErrEmbarcacion().setText("Barco requerido");
        }
        if (!v5) {
            view.getErrDestino().setText("Destino requerido");
        }
        if (!v3) {
            view.getErrMeta().setText("Meta numérica inválida");
        }
        if (!v4) {
            view.getErrFecha().setText(errmsg);
        }

        if (!(v1 && v2 && v3 && v4 && v5)) {
            return null;
        }

        try {
            Planificacion p = new Planificacion();
            p.setCodigoViaje(view.getTxtCodigo().getText().trim());
            p.setEmbarcacionNombre(view.getTxtEmbarcacion().getText().trim());
            p.setDestinoRuta(view.getTxtDestino().getText().trim());
            p.setFechaSalidaProgramada(new Date(
                    ((java.util.Date) view.getSpinFechaSalida().getValue()).getTime()));
            p.setMetaPesoKg(Double.parseDouble(view.getTxtMetaPeso().getText()));
            p.setEstado(view.getCbEstado().getSelectedItem().toString());
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
