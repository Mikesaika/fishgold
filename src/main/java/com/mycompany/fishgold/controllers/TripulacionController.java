package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.TripulacionPanel;
import javax.swing.*;
import java.util.List;
//Controlador para la gestión de Tripulación.
//Centraliza la asignación de personal a faenas activas.

public class TripulacionController {
    private final TripulacionPanel view;
    private final TripulacionDAO dao;
    private final FaenaDAO faenaDAO;
    private final TrabajadorDAO trabajadorDAO;
    private final CargoDAO cargoDAO;

    public TripulacionController(TripulacionPanel view, TripulacionDAO dao, FaenaDAO faenaDAO,
            TrabajadorDAO trabajadorDAO, CargoDAO cargoDAO) {
        this.view = view;
        this.dao = dao;
        this.faenaDAO = faenaDAO;
        this.trabajadorDAO = trabajadorDAO;
        this.cargoDAO = cargoDAO;
        init();
    }

    private void init() {
        refreshData();

        // Listener para actualizar tabla al cambiar de faena
        view.getCbFaena().addActionListener(e -> cargarTabla());
        // Asignación de Capitán (Regla de negocio: solo con licencia)
        view.getBtnAsignarCapitan().addActionListener(e -> ejecutarAsignacion(true));
        // Asignación de otros cargos
        view.getBtnAsignarTrabajador().addActionListener(e -> ejecutarAsignacion(false));
        view.getBtnEliminar().addActionListener(e -> eliminar());
    }

    public void refreshData() {
        cargarCombos();
        cargarTabla();
    }

    private void cargarCombos() {
        // Cargar Faenas Pendientes/En Curso
        DefaultComboBoxModel<Faena> faenaModel = new DefaultComboBoxModel<>();
        faenaDAO.readAll().stream()
                .filter(f -> !"Finalizada".equals(f.getEstado()))
                .forEach(faenaModel::addElement);
        view.getCbFaena().setModel(faenaModel);

        // Cargar Cargos desde la base de datos
        DefaultComboBoxModel<Cargo> cargoModel = new DefaultComboBoxModel<>();
        cargoDAO.readAll().forEach(cargoModel::addElement);
        view.getCbCargo().setModel(cargoModel);

        // Cargar Trabajadores Activos
        DefaultComboBoxModel<Trabajador> capModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Trabajador> trabModel = new DefaultComboBoxModel<>();

        List<Trabajador> trabajadores = trabajadorDAO.readAll();
        for (Trabajador t : trabajadores) {
            if ("Activo".equals(t.getEstado())) {
                trabModel.addElement(t);
                if (t.isTieneLicencia())
                    capModel.addElement(t);
            }
        }
        view.getCbCapitan().setModel(capModel);
        view.getCbTrabajador().setModel(trabModel);
    }

    private void cargarTabla() {
        view.getTableModel().setRowCount(0);
        Faena faena = (Faena) view.getCbFaena().getSelectedItem();
        if (faena == null)
            return;

        List<Tripulante> list = dao.readByFaena(faena.getId());
        for (Tripulante t : list) {
            view.getTableModel().addRow(new Object[] {
                    t.getId(), t.getTrabajadorNombre(), t.getNombreCargo(), t.getDescripcion()
            });
        }
    }

    private void ejecutarAsignacion(boolean esCapitan) {
        Faena faena = (Faena) view.getCbFaena().getSelectedItem();
        if (faena == null) {
            mostrarMensaje("Seleccione una faena primero.");
            return;
        }

        Trabajador trab = esCapitan ? (Trabajador) view.getCbCapitan().getSelectedItem()
                : (Trabajador) view.getCbTrabajador().getSelectedItem();

        Cargo cargoObj = esCapitan ? cargoDAO.readByName("Capitán")
                : (Cargo) view.getCbCargo().getSelectedItem();

        if (trab == null || cargoObj == null) {
            mostrarMensaje("Faltan datos de selección.");
            return;
        }

        // VALIDACIÓN DE REGLAS DE NEGOCIO
        if (validarDuplicadoYRol(faena.getId(), trab.getId(), cargoObj.getNombreCargo())) {
            Tripulante t = new Tripulante();
            t.setFaenaId(faena.getId());
            t.setTrabajadorId(trab.getId());
            t.setCargoId(cargoObj.getId()); // Usamos ID de cargo, no String quemado
            t.setDescripcion(view.getTxtDescripcion().getText().trim());

            if (dao.create(t)) {
                mostrarMensaje("Asignación exitosa.");
                view.getTxtDescripcion().setText("");
                cargarTabla();
            }
        }
    }

    private boolean validarDuplicadoYRol(int faenaId, int trabId, String nombreCargo) {
        List<Tripulante> actuales = dao.readByFaena(faenaId);
        for (Tripulante t : actuales) {
            if (t.getTrabajadorId() == trabId) {
                mostrarMensaje("Este trabajador ya forma parte de esta faena.");
                return false;
            }
            if ("Capitán".equals(nombreCargo) && "Capitán".equals(t.getNombreCargo())) {
                mostrarMensaje("La faena ya cuenta con un capitán asignado.");
                return false;
            }
        }
        return true;
    }

    private void eliminar() {
        int row = view.getTable().getSelectedRow();
        if (row == -1)
            return;

        int id = (int) view.getTableModel().getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(view, "¿Remover tripulante?", "Confirmar", 0) == 0) {
            if (dao.delete(id))
                cargarTabla();
        }
    }

    private void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Gestión de Tripulación", JOptionPane.INFORMATION_MESSAGE);
    }
}