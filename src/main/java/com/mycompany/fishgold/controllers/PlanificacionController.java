package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.PlanificacionPanel;
import com.mycompany.fishgold.util.Validator;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.util.List;

public class PlanificacionController {
    private final PlanificacionPanel view;
    private final PlanificacionDAO dao;
    private final TrabajadorDAO trabDAO;

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
            if (!e.getValueIsAdjusting())
                seleccionarFila();
        });

        recargarTabla();
    }

    public void recargarTabla() {
        cargarTabla(dao.readAll());
        cargarTripulacionDisponible();
    }

    private void cargarTripulacionDisponible() {
        view.getListModelTrabajadores().clear();
        // Carga trabajadores activos que no están en viajes pendientes/en curso
        List<Trabajador> disponibles = trabDAO.readDisponibles();
        for (Trabajador t : disponibles) {
            view.getListModelTrabajadores().addElement(t);
        }
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
        Planificacion p = extraerDatosDeVista();
        List<Trabajador> seleccionados = view.getListTrabajadores().getSelectedValuesList();

        if (p == null)
            return;

        if (seleccionados.isEmpty()) {
            view.getErrTripulacion().setText("⚠ Seleccione al menos un tripulante");
            return;
        }

        if (dao.createConTripulacion(p, seleccionados)) {
            JOptionPane.showMessageDialog(view, "🚢 Viaje y Tripulación registrados exitosamente.");
            limpiarFormulario();
            recargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Error al registrar: Verifique que el código no esté duplicado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione una planificación de la tabla.");
            return;
        }

        Planificacion p = extraerDatosDeVista();
        if (p == null)
            return;

        p.setId((int) view.getTable().getValueAt(fila, 0));

        // Obtenemos la tripulación seleccionada (si el usuario quiere cambiarla)
        List<Trabajador> nuevaTripulacion = view.getListTrabajadores().getSelectedValuesList();

        // Lógica de experto: Si se seleccionaron nuevos trabajadores, se actualiza la
        // tripulación también
        boolean exito;
        if (!nuevaTripulacion.isEmpty()) {
            exito = dao.updateConTripulacion(p, nuevaTripulacion);
        } else {
            // Si no seleccionó a nadie nuevo, solo actualizamos los datos del viaje
            exito = dao.update(p);
        }

        if (exito) {
            JOptionPane.showMessageDialog(view, "🔄 Planificación actualizada correctamente.");
            limpiarFormulario();
            recargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Fallo al actualizar los datos en la base de datos.");
        }
    }

    private void eliminar() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1)
            return;

        int id = (int) view.getTable().getValueAt(fila, 0);
        String codigo = view.getTable().getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Eliminar el viaje " + codigo + "?\nSe borrará también el historial de tripulación asociado.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(view, "🗑 Registro eliminado permanentemente.");
                limpiarFormulario();
                recargarTabla();
            } else {
                JOptionPane.showMessageDialog(view,
                        "No se pudo eliminar: El registro podría estar vinculado a una liquidación final.");
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
        view.getListTrabajadores().clearSelection();
        view.getTable().clearSelection();

        view.getErrCodigo().setText(" ");
        view.getErrEmbarcacion().setText(" ");
        view.getErrMeta().setText(" ");
        view.getErrTripulacion().setText(" ");
    }

    private void seleccionarFila() {
        int fila = view.getTable().getSelectedRow();
        if (fila != -1) {
            view.getTxtCodigo().setText(view.getTable().getValueAt(fila, 1).toString());
            view.getTxtEmbarcacion().setText(view.getTable().getValueAt(fila, 2).toString());
            view.getTxtDestino().setText(view.getTable().getValueAt(fila, 3).toString());

            Object fecha = view.getTable().getValueAt(fila, 4);
            if (fecha instanceof Date)
                view.getSpinFechaSalida().setValue(new java.util.Date(((Date) fecha).getTime()));

            view.getTxtMetaPeso().setText(view.getTable().getValueAt(fila, 5).toString());
            view.getCbEstado().setSelectedItem(view.getTable().getValueAt(fila, 6).toString());

            // Nota: Podrías cargar los trabajadores actuales de ese viaje en la lista si lo
            // deseas,
            // pero como la lista es de "Disponibles", dejarlos deseleccionados es lo
            // estándar.
        }
    }

    private Planificacion extraerDatosDeVista() {
        view.getErrCodigo().setText(" ");
        view.getErrEmbarcacion().setText(" ");
        view.getErrMeta().setText(" ");

        boolean v1 = Validator.isNotBlank(view.getTxtCodigo());
        boolean v2 = Validator.isNotBlank(view.getTxtEmbarcacion());
        boolean v3 = Validator.isDecimal(view.getTxtMetaPeso());

        if (!v1)
            view.getErrCodigo().setText("⚠ Código requerido");
        if (!v2)
            view.getErrEmbarcacion().setText("⚠ Barco requerido");
        if (!v3)
            view.getErrMeta().setText("⚠ Meta numérica inválida");

        if (!(v1 && v2 && v3))
            return null;

        try {
            Planificacion p = new Planificacion();
            p.setCodigoViaje(view.getTxtCodigo().getText().trim());
            p.setEmbarcacionNombre(view.getTxtEmbarcacion().getText().trim());
            p.setDestinoRuta(view.getTxtDestino().getText().trim());
            p.setFechaSalidaProgramada(new Date(((java.util.Date) view.getSpinFechaSalida().getValue()).getTime()));
            p.setMetaPesoKg(Double.parseDouble(view.getTxtMetaPeso().getText()));
            p.setEstado(view.getCbEstado().getSelectedItem().toString());
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}