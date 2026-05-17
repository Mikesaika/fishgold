package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.FaenaAsistenciaPanel;
import com.mycompany.fishgold.views.FaenaAsistenciaPanel.TripulanteFila;
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
        view.getBtnGuardarTodo().addActionListener(e -> guardarTodo());
        view.getBtnRefresh().addActionListener(e -> recargarVista());
        view.getCbPlanificacion().addActionListener(e -> cargarTripulacionDelViaje());
        view.getBtnEliminar().addActionListener(e -> eliminarAsistencia());

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

        recargarVista();
    }

    public void recargarVista() {
        cargarCombos();
        cargarTabla(dao.readAll());
        cargarTripulacionDelViaje();
    }

    private void cargarCombos() {
        view.getCbPlanificacion().removeAllItems();
        planDAO.readAll().stream()
                .filter(p -> p.isActivo())
                .filter(p -> !"Finalizado".equalsIgnoreCase(p.getEstado()))
                .filter(p -> !"Cancelada".equalsIgnoreCase(p.getEstado()))
                .filter(p -> planDAO.countLiquidaciones(p.getId()) == 0)
                .forEach(view.getCbPlanificacion()::addItem);
    }

    private void cargarTripulacionDelViaje() {
        view.limpiarFilasTripulantes();
        view.getErrPlanificacion().setText(" ");

        Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
        if (p == null) {
            return;
        }

        for (Trabajador t : trabDAO.readByPlanificacion(p.getId())) {
            FaenaAsistencia fa = dao.findByPlanificacionYTrabajador(p.getId(), t.getId());
            String est = fa != null ? fa.getEstadoAsistencia() : "Presente";
            view.agregarFilaTripulante(t, est);
        }
        view.finalizarFilas();
    }

    private boolean viajePermiteAsistencia(Planificacion p) {
        if (p == null) {
            view.getErrPlanificacion().setText("Seleccione un viaje.");
            return false;
        }
        if ("Finalizado".equalsIgnoreCase(p.getEstado()) || "Cancelada".equalsIgnoreCase(p.getEstado())) {
            JOptionPane.showMessageDialog(view,
                    "No se puede registrar asistencia: el viaje está cerrado o cancelado.",
                    "Viaje no disponible", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (planDAO.countLiquidaciones(p.getId()) > 0) {
            JOptionPane.showMessageDialog(view,
                    "No se puede registrar asistencia: el viaje ya tiene liquidación.",
                    "Viaje liquidado", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        view.getErrPlanificacion().setText(" ");
        return true;
    }

private void guardarTodo() {
        Planificacion p = (Planificacion) view.getCbPlanificacion().getSelectedItem();
        if (!viajePermiteAsistencia(p)) {
            return;
        }

        List<TripulanteFila> filas = view.getFilasTripulantes();
        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay tripulantes asignados a este viaje.");
            return;
        }

        // ¡AQUÍ ESTÁ LA SOLUCIÓN! Declaramos la variable 'ok' como entero e inicializamos en 0
        int ok = 0; 

        for (TripulanteFila fila : filas) {
            Trabajador t = fila.getTrabajador();
            String estado = fila.getEstadoParaGuardar();
            FaenaAsistencia existente = dao.findByPlanificacionYTrabajador(p.getId(), t.getId());
            if (existente != null) {
                if (!estado.equals(existente.getEstadoAsistencia())) {
                    if (dao.updateEstado(existente.getId(), estado)) {
                        ok++;
                    }
                } else {
                    ok++;
                }
                fila.refrescarDesdeBd(estado);
            } else {
                FaenaAsistencia fa = new FaenaAsistencia();
                fa.setPlanificacionId(p.getId());
                fa.setTrabajadorId(t.getId());
                fa.setEstadoAsistencia(estado);
                if (dao.create(fa)) {
                    ok++;
                    fila.refrescarDesdeBd(estado);
                }
            }
        }

        JOptionPane.showMessageDialog(view,
                "Se procesaron las asistencias del viaje " + p.getCodigoViaje() + " (" + ok + " registros).");
        cargarTabla(dao.readAll());
    }
    private void cargarTabla(List<FaenaAsistencia> lista) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        for (FaenaAsistencia fa : lista) {
            model.addRow(new Object[] {
                    fa.getId(),
                    fa.getPlanificacionCodigo(),
                    fa.getTrabajadorNombre(),
                    fa.getEstadoAsistencia(),
                    fa.getFechaAsistencia()
            });
        }
    }

    private void eliminarAsistencia() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view,
                    "Seleccione un registro del historial para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) view.getTable().getValueAt(fila, 0);
        String codigoViaje = view.getTable().getValueAt(fila, 1).toString();
        String nombreTripulante = view.getTable().getValueAt(fila, 2).toString();

        // Verificar si el viaje ya tiene liquidación (no se puede tocar)
        FaenaAsistencia registroActual = null;
        for (FaenaAsistencia fa : dao.readAll()) {
            if (fa.getId() == id) {
                registroActual = fa;
                break;
            }
        }
        if (registroActual != null && planDAO.countLiquidaciones(registroActual.getPlanificacionId()) > 0) {
            JOptionPane.showMessageDialog(view,
                    "No se puede eliminar: el viaje '" + codigoViaje + "' ya tiene liquidación registrada.",
                    "Operación no permitida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Eliminar la asistencia de '" + nombreTripulante + "' en el viaje '" + codigoViaje + "'?\n"
                        + "Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(view, "Registro de asistencia eliminado correctamente.");
                cargarTabla(dao.readAll());
                cargarTripulacionDelViaje();
            } else {
                JOptionPane.showMessageDialog(view, "Error al eliminar el registro.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
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
}
