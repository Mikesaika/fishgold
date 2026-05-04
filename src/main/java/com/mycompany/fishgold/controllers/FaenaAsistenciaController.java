package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.FaenaAsistenciaPanel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FaenaAsistenciaController {
    private final FaenaAsistenciaPanel view;
    private final FaenaAsistenciaDAO dao;
    private final PlanificacionDAO planDAO;
    private final TrabajadorDAO trabDAO;

    public FaenaAsistenciaController(FaenaAsistenciaPanel view, FaenaAsistenciaDAO dao, PlanificacionDAO planDAO,
            TrabajadorDAO trabDAO) {
        this.view = view;
        this.dao = dao;
        this.planDAO = planDAO;
        this.trabDAO = trabDAO;
        init();
    }

    private void init() {
        // Listeners de botones
        view.getBtnAdd().addActionListener(e -> registrarAsistencia());
        view.getBtnRefresh().addActionListener(e -> recargarVista());
        view.getBtnClear().addActionListener(e -> limpiarFormulario());

        // BÚSQUEDA EN TIEMPO REAL: Implementación de DocumentListener
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

        // Sincronización de tabla
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        recargarVista();
    }

    public void recargarVista() {
        cargarCombos();
        cargarTabla(dao.readAll());
        limpiarFormulario();
    }

    private void cargarCombos() {
        view.getCbPlanificacion().removeAllItems();
        view.getCbTrabajador().removeAllItems();

        // Filtro: Solo viajes no finalizados
        planDAO.readAll().stream()
                .filter(p -> !p.getEstado().equalsIgnoreCase("Finalizado"))
                .forEach(view.getCbPlanificacion()::addItem);

        // Filtro: Solo personal activo
        trabDAO.readAll().stream()
                .filter(t -> t.getEstado().equalsIgnoreCase("Activo"))
                .forEach(view.getCbTrabajador()::addItem);
    }

    private void cargarTabla(List<FaenaAsistencia> lista) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        for (FaenaAsistencia fa : lista) {
            model.addRow(new Object[] {
                    fa.getId(), // Sigue en el modelo pero oculto en la vista
                    fa.getPlanificacionCodigo(),
                    fa.getTrabajadorNombre(),
                    fa.getFechaAsistencia()
            });
        }
    }

    private void registrarAsistencia() {
        // Limpiar errores visuales previos
        view.getErrPlanificacion().setText(" ");
        view.getErrTrabajador().setText(" ");

        Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
        Trabajador t = (Trabajador) view.getCbTrabajador().getSelectedItem();

        // Validación con feedback en los nuevos labels de la vista
        if (p == null) {
            view.getErrPlanificacion().setText("⚠ Seleccione un viaje");
            return;
        }
        if (t == null) {
            view.getErrTrabajador().setText("⚠ Seleccione un trabajador");
            return;
        }

        FaenaAsistencia fa = new FaenaAsistencia();
        fa.setPlanificacionId(p.getId());
        fa.setTrabajadorId(t.getId());

        if (dao.create(fa)) {
            JOptionPane.showMessageDialog(view, "✅ Asistencia confirmada.");
            recargarVista();
        } else {
            JOptionPane.showMessageDialog(view, "Este trabajador ya está registrado en este viaje.",
                    "Registro Duplicado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String query = view.getTxtSearch().getText().trim();
        if (query.isEmpty()) {
            cargarTabla(dao.readAll());
        } else {
            cargarTabla(dao.search(query));
        }
    }

    private void limpiarFormulario() {
        if (view.getCbPlanificacion().getItemCount() > 0)
            view.getCbPlanificacion().setSelectedIndex(0);
        if (view.getCbTrabajador().getItemCount() > 0)
            view.getCbTrabajador().setSelectedIndex(0);

        view.getErrPlanificacion().setText(" ");
        view.getErrTrabajador().setText(" ");
        view.getTable().clearSelection();
    }

    private void seleccionarFila() {
        int fila = view.getTable().getSelectedRow();
        if (fila != -1) {
            // Log informativo usando la columna oculta ID (índice 0)
            System.out.println("ID Asistencia: " + view.getTable().getValueAt(fila, 0));
        }
    }
}