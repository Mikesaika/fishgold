package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.Embarcacion;
import com.mycompany.fishgold.models.EmbarcacionDAO;
import com.mycompany.fishgold.views.EmbarcacionPanel;
import com.mycompany.fishgold.util.Validator;
import java.awt.Color;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class EmbarcacionController {
    private final EmbarcacionPanel view;
    private final EmbarcacionDAO dao;
    private int idEditando = -1;

    public EmbarcacionController(EmbarcacionPanel view, EmbarcacionDAO dao) {
        this.view = view;
        this.dao = dao;
        init();
    }

    private void init() {
        cargarTabla();
        setupRealTimeValidation();

        view.getBtnGuardar().addActionListener(e -> guardar());
        view.getBtnLimpiar().addActionListener(e -> limpiarFormulario());
        view.getBtnEditar().addActionListener(e -> cargarParaEdicion());
        view.getBtnEliminar().addActionListener(e -> eliminar());
    }

    // Validación en tiempo real campo por campo ---
    private void setupRealTimeValidation() {
        addValidation(view.getTxtCapacidad(), "NUMERIC");
        addValidation(view.getTxtAnioCompra(), "YEAR");
        addValidation(view.getTxtMatricula(), "NOT_BLANK");
    }

    private void addValidation(JTextField field, String type) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            public void changedUpdate(DocumentEvent e) {
                validate();
            }

            private void validate() {
                boolean valid = true;
                String text = field.getText();

                switch (type) {
                    case "NUMERIC":
                        valid = Validator.isNumeric(text);
                        break;
                    case "YEAR":
                        valid = Validator.isValidYear(text);
                        break;
                    case "NOT_BLANK":
                        valid = Validator.isNotBlank(text);
                        break;
                }

                field.setBackground(valid ? Color.WHITE : new Color(255, 220, 220));
            }
        });
    }

    private void cargarTabla() {
        view.getTableModel().setRowCount(0);
        List<Embarcacion> list = dao.readAll();
        list.forEach(e -> {
            view.getTableModel().addRow(new Object[] {
                    e.getId(), e.getNombre(), e.getPropietario(), e.getModelo(),
                    e.getCapacidad(), e.getAnioCompra(), e.getMatricula(), e.getEstado()
            });
        });
    }

    private void guardar() {
        // Validación antes de procesar
        if (!validateForm())
            return;

        Embarcacion e = new Embarcacion();
        e.setNombre(view.getTxtNombre().getText().trim());
        e.setPropietario(view.getTxtPropietario().getText().trim());
        e.setModelo(view.getTxtModelo().getText().trim());
        e.setCapacidad(Integer.parseInt(view.getTxtCapacidad().getText()));
        e.setAnioCompra(Integer.parseInt(view.getTxtAnioCompra().getText()));
        e.setMatricula(view.getTxtMatricula().getText().trim());
        e.setEstado(view.getCbEstado().getSelectedItem().toString());

        boolean exito;
        if (idEditando == -1) {
            exito = dao.create(e);
        } else {
            e.setId(idEditando); // Primero asignamos el ID
            exito = dao.update(e);
        }

        if (exito) {
            JOptionPane.showMessageDialog(view, "Operación exitosa.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(view, "Error: Verifique que la matrícula no exista.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (!Validator.isNotBlank(view.getTxtNombre().getText()) ||
                !Validator.isNotBlank(view.getTxtMatricula().getText())) {
            JOptionPane.showMessageDialog(view, "Nombre y Matrícula son obligatorios.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isValidYear(view.getTxtAnioCompra().getText())) {
            JOptionPane.showMessageDialog(view, "El año de compra no es válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void cargarParaEdicion() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un registro de la tabla.");
            return;
        }

        idEditando = (int) view.getTableModel().getValueAt(row, 0);
        view.getTxtNombre().setText(view.getTableModel().getValueAt(row, 1).toString());
        view.getTxtPropietario().setText(view.getTableModel().getValueAt(row, 2).toString());
        view.getTxtModelo().setText(view.getTableModel().getValueAt(row, 3).toString());
        view.getTxtCapacidad().setText(view.getTableModel().getValueAt(row, 4).toString());
        view.getTxtAnioCompra().setText(view.getTableModel().getValueAt(row, 5).toString());
        view.getTxtMatricula().setText(view.getTableModel().getValueAt(row, 6).toString());
        view.getCbEstado().setSelectedItem(view.getTableModel().getValueAt(row, 7).toString());
    }

    private void eliminar() {
        int row = view.getTable().getSelectedRow();
        if (row == -1)
            return;

        int id = (int) view.getTableModel().getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(view, "¿Borrar registro?", "Confirmar", 0) == 0) {
            if (dao.delete(id)) {
                cargarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(view, "No se puede eliminar: tiene faenas asociadas.");
            }
        }
    }

    private void limpiarFormulario() {
        idEditando = -1;
        view.getTxtNombre().setText("");
        view.getTxtPropietario().setText("");
        view.getTxtModelo().setText("");
        view.getTxtCapacidad().setText("");
        view.getTxtAnioCompra().setText("");
        view.getTxtMatricula().setText("");
        view.getTxtCapacidad().setBackground(Color.WHITE);
        view.getTxtAnioCompra().setBackground(Color.WHITE);
        view.getCbEstado().setSelectedIndex(0);
        view.getTable().clearSelection();
    }
}