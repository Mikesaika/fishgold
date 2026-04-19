package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.TrabajadorPanel;
import com.mycompany.fishgold.util.Validator;
import java.awt.Color;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class TrabajadorController {
    private final TrabajadorPanel view;
    private final TrabajadorDAO dao;
    private final CargoDAO cargoDAO;
    private int idEditando = -1;

    public TrabajadorController(TrabajadorPanel view, TrabajadorDAO dao, CargoDAO cargoDAO) {
        this.view = view;
        this.dao = dao;
        this.cargoDAO = cargoDAO;
        init();
    }

    private void init() {
        cargarTabla();
        setupRealTimeValidation();
        view.getCbPuestos().addActionListener(e -> {
            boolean isOtro = "Otro".equals(view.getCbPuestos().getSelectedItem());
            view.getTxtOtroPuesto().setEnabled(isOtro);
            if (!isOtro)
                view.getTxtOtroPuesto().setText("");
        });

        view.getBtnGuardar().addActionListener(e -> guardar());
        view.getBtnLimpiar().addActionListener(e -> limpiarFormulario());
        view.getBtnEditar().addActionListener(e -> cargarParaEdicion());
        view.getBtnEliminar().addActionListener(e -> eliminar());
    }

    private void setupRealTimeValidation() {
        addLiveValidation(view.getTxtNombre(), "ALPHA");
        addLiveValidation(view.getTxtEmergTelefono(), "PHONE");
    }

    private void addLiveValidation(JTextField field, String type) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                check();
            }

            public void removeUpdate(DocumentEvent e) {
                check();
            }

            public void changedUpdate(DocumentEvent e) {
                check();
            }

            private void check() {
                String val = field.getText().trim();
                boolean ok = type.equals("ALPHA") ? Validator.isAlpha(val) : Validator.isValidPhone(val);
                field.setBackground(ok || val.isEmpty() ? Color.WHITE : new Color(255, 210, 210));
            }
        });
    }

    private void cargarTabla() {
        view.getTableModel().setRowCount(0);
        List<Trabajador> list = dao.readAll();
        for (Trabajador t : list) {
            view.getTableModel().addRow(new Object[] {
                    t.getId(),
                    t.getNombreCompleto(),
                    t.isTieneLicencia() ? "Sí" : "No",
                    t.getDireccion(),
                    t.getContactoEmergenciaNombre() + " (" + t.getContactoEmergenciaRelacion() + ")",
                    t.getContactoEmergenciaTelefono(),
                    t.getPuestosAnteriores(),
                    t.getEstado()
            });
        }
    }

    private void guardar() {
        String nombre = view.getTxtNombre().getText().trim();
        String cargoSelected = (String) view.getCbPuestos().getSelectedItem();
        String cargoFinal = "Otro".equals(cargoSelected) ? view.getTxtOtroPuesto().getText().trim() : cargoSelected;

        if (!Validator.isNotBlank(nombre) || !Validator.isNotBlank(view.getTxtEmergTelefono().getText())) {
            JOptionPane.showMessageDialog(view, "Nombre y Teléfono son obligatorios.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Trabajador t = new Trabajador();
        t.setNombreCompleto(nombre);
        t.setTieneLicencia(view.isLicencia());
        t.setDireccion(view.getTxtDireccion().getText().trim());
        t.setContactoEmergenciaNombre(view.getTxtEmergNombre().getText().trim());
        t.setContactoEmergenciaRelacion(view.getTxtEmergRelacion().getText().trim());
        t.setContactoEmergenciaTelefono(view.getTxtEmergTelefono().getText().trim());
        t.setPuestosAnteriores(cargoFinal);
        t.setEstado(view.getCbEstado().getSelectedItem().toString());

        boolean exito;
        if (idEditando == -1) {
            exito = dao.create(t);
        } else {
            t.setId(idEditando);
            exito = dao.update(t);
        }

        if (exito) {
            JOptionPane.showMessageDialog(view, "Operación exitosa.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Error al guardar en la base de datos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarParaEdicion() {
        int row = view.getTable().getSelectedRow();
        if (row == -1)
            return;

        idEditando = (int) view.getTableModel().getValueAt(row, 0);
        Trabajador t = dao.readById(idEditando);

        if (t != null) {
            view.getTxtNombre().setText(t.getNombreCompleto());
            view.setLicencia(t.isTieneLicencia());
            view.getTxtDireccion().setText(t.getDireccion());
            view.getTxtEmergNombre().setText(t.getContactoEmergenciaNombre());
            view.getTxtEmergRelacion().setText(t.getContactoEmergenciaRelacion());
            view.getTxtEmergTelefono().setText(t.getContactoEmergenciaTelefono());
            view.getCbEstado().setSelectedItem(t.getEstado());

            String p = t.getPuestosAnteriores();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) view.getCbPuestos().getModel();
            if (model.getIndexOf(p) != -1) {
                view.getCbPuestos().setSelectedItem(p);
            } else {
                view.getCbPuestos().setSelectedItem("Otro");
                view.getTxtOtroPuesto().setText(p);
            }
        }
    }

    private void eliminar() {
        int row = view.getTable().getSelectedRow();
        if (row == -1)
            return;

        int id = (int) view.getTableModel().getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(view, "¿Eliminar trabajador?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                cargarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(view, "No se puede eliminar: tiene historial de faenas.");
            }
        }
    }

    private void limpiarFormulario() {
        idEditando = -1;
        view.getTxtNombre().setText("");
        view.setLicencia(false);
        view.getTxtDireccion().setText("");
        view.getTxtEmergNombre().setText("");
        view.getTxtEmergRelacion().setText("");
        view.getTxtEmergTelefono().setText("");
        view.getTxtOtroPuesto().setText("");
        view.getTxtOtroPuesto().setEnabled(false);
        view.getCbPuestos().setSelectedIndex(0);
        view.getTable().clearSelection();
    }
}