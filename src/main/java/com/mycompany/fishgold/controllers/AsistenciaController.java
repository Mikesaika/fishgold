package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.AsistenciaPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
// Controlador para la gestión de asistencias de embarco y desembarco.

public class AsistenciaController {

    private final AsistenciaPanel view;
    private final TripulacionDAO tripulacionDAO;
    private final FaenaDAO faenaDAO;

    public AsistenciaController(AsistenciaPanel view, TripulacionDAO tripulacionDAO, FaenaDAO faenaDAO) {
        this.view = view;
        this.tripulacionDAO = tripulacionDAO;
        this.faenaDAO = faenaDAO;
        init();
    }

    private void init() {
        cargarFaenasActivas();
        // Listeners para actualización automática al cambiar faena en los combos
        view.getCbFaenaEmbarco().addActionListener(e -> updateTableEmbarco());
        view.getCbFaenaDesembarco().addActionListener(e -> updateTableDesembarco());
        view.getCbFaenaReporte().addActionListener(e -> cargarTablaReporte());
        // Listeners para los botones de acción
        view.getBtnMarcarEmbarco().addActionListener(e -> handleAsistenciaAction("EMBARCO"));
        view.getBtnMarcarDesembarco().addActionListener(e -> handleAsistenciaAction("DESEMBARCO"));
        // Refresco al cambiar de pestaña
        view.getTabbedPane().addChangeListener(e -> cargarFaenasActivas());
    }

    public void cargarFaenasActivas() {
        DefaultComboBoxModel<Faena> modelEmb = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Faena> modelDes = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Faena> modelRep = new DefaultComboBoxModel<>();

        List<Faena> faenas = faenaDAO.readAll();

        for (Faena f : faenas) {
            modelRep.addElement(f);
            // Solo faenas pendientes o en curso permiten marcar asistencia
            if (!"Finalizada".equalsIgnoreCase(f.getEstado())) {
                modelEmb.addElement(f);
                modelDes.addElement(f);
            }
        }

        view.getCbFaenaEmbarco().setModel(modelEmb);
        view.getCbFaenaDesembarco().setModel(modelDes);
        view.getCbFaenaReporte().setModel(modelRep);

        // Actualizar tablas inicialmente
        updateTableEmbarco();
        updateTableDesembarco();
    }

    private void handleAsistenciaAction(String tipo) {
        JTable tabla = tipo.equals("EMBARCO") ? view.getTableEmbarco() : view.getTableDesembarco();
        int selectedRow = tabla.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un tripulante de la lista.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTripulante = (int) tabla.getValueAt(selectedRow, 0);
        String estadoActual = (String) tabla.getValueAt(selectedRow, 3);

        // Lógica de rotación: null -> Presente -> Ausente -> null
        Boolean nuevoEstado = rotarEstado(estadoActual);

        boolean exito = (tipo.equals("EMBARCO"))
                ? tripulacionDAO.updateAsistenciaEmbarco(idTripulante, nuevoEstado)
                : tripulacionDAO.updateAsistenciaDesembarco(idTripulante, nuevoEstado);

        if (exito) {
            if (tipo.equals("EMBARCO"))
                updateTableEmbarco();
            else
                updateTableDesembarco();
        }
    }

    private Boolean rotarEstado(String actual) {
        switch (actual) {
            case "Presente":
                return false; // Pasa a Ausente
            case "Ausente":
                return null; // Pasa a No registrado
            default:
                return true; // Pasa a Presente
        }
    }

    private void updateTableEmbarco() {
        fillTable(view.getModelEmbarco(), (Faena) view.getCbFaenaEmbarco().getSelectedItem(), true);
    }

    private void updateTableDesembarco() {
        fillTable(view.getModelDesembarco(), (Faena) view.getCbFaenaDesembarco().getSelectedItem(), false);
    }

    private void fillTable(DefaultTableModel model, Faena faena, boolean esEmbarco) {
        model.setRowCount(0);
        if (faena == null)
            return;

        List<Tripulante> tripulacion = tripulacionDAO.readByFaena(faena.getId());
        for (Tripulante t : tripulacion) {
            Boolean asist = esEmbarco ? t.getAsistenciaEmbarco() : t.getAsistenciaDesembarco();
            model.addRow(new Object[] {
                    t.getId(),
                    t.getTrabajadorNombre(),
                    t.getNombreCargo(),
                    formatStatus(asist)
            });
        }
    }

    private void cargarTablaReporte() {
        DefaultTableModel model = view.getModelReporte();
        model.setRowCount(0);
        Faena f = (Faena) view.getCbFaenaReporte().getSelectedItem();

        if (f == null)
            return;

        List<Tripulante> list = tripulacionDAO.readByFaena(f.getId());
        for (Tripulante t : list) {
            model.addRow(new Object[] {
                    t.getId(),
                    t.getTrabajadorNombre(),
                    t.getNombreCargo(),
                    formatStatus(t.getAsistenciaEmbarco()),
                    formatStatus(t.getAsistenciaDesembarco())
            });
        }
    }

    private String formatStatus(Boolean b) {
        if (b == null)
            return "No registrado";
        return b ? "Presente" : "Ausente";
    }
}