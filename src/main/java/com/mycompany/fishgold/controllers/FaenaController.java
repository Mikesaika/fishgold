package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.*;
import com.mycompany.fishgold.views.FaenaPanel;
import com.mycompany.fishgold.util.Validator;

import javax.swing.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class FaenaController {
    private final FaenaPanel view;
    private final FaenaDAO dao;
    private final EmbarcacionDAO embarcacionDAO;
    private int idEditando = -1;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public FaenaController(FaenaPanel view, FaenaDAO dao, EmbarcacionDAO embarcacionDAO) {
        this.view = view;
        this.dao = dao;
        this.embarcacionDAO = embarcacionDAO;
        init();
    }

    private void init() {
        refreshData();

        view.getBtnGuardar().addActionListener(e -> guardar());
        view.getBtnLimpiar().addActionListener(e -> limpiarFormulario());
        view.getBtnEditar().addActionListener(e -> cargarParaEdicion());
        view.getBtnEliminar().addActionListener(e -> eliminar());

        setupRealTimeValidation();
    }

    public void refreshData() {
        cargarEmbarcaciones();
        cargarTabla();
    }

    private void setupRealTimeValidation() {
        view.getTxtFecha().setToolTipText("Formato esperado: AAAA-MM-DD");
        view.getTxtHora().setToolTipText("Formato esperado: HH:MM:SS");
    }

    public void cargarEmbarcaciones() {
        DefaultComboBoxModel<Embarcacion> model = new DefaultComboBoxModel<>();
        List<Embarcacion> embarcaciones = embarcacionDAO.readAll();

        for (Embarcacion e : embarcaciones) {
            // Solo barcos activos
            if ("Activa".equals(e.getEstado())) {
                model.addElement(e);
            }
        }
        view.getCbEmbarcacion().setModel(model);
    }

    private void cargarTabla() {
        view.getTableModel().setRowCount(0);
        List<Faena> list = dao.readAll();
        for (Faena f : list) {
            view.getTableModel().addRow(new Object[] {
                    f.getId(),
                    f.getNombre(),
                    f.getFecha() != null ? df.format(f.getFecha()) : "",
                    f.getHoraEmbarco() != null ? f.getHoraEmbarco().toString() : "",
                    f.getEmbarcacionNombre(),
                    f.getRuta(),
                    f.getEstado()
            });
        }
    }

    private void guardar() {
        if (!validarCampos())
            return;

        try {
            Faena f = new Faena();
            f.setNombre(view.getTxtNombre().getText().trim());
            f.setFecha(df.parse(view.getTxtFecha().getText().trim()));
            f.setHoraEmbarco(Time.valueOf(view.getTxtHora().getText().trim()));
            f.setRuta(view.getTxtRuta().getText().trim());
            f.setEstado(view.getCbEstado().getSelectedItem().toString());

            Embarcacion emb = (Embarcacion) view.getCbEmbarcacion().getSelectedItem();
            if (emb != null)
                f.setEmbarcacionId(emb.getId());

            boolean exito;

            if (idEditando == -1) {
                exito = dao.create(f);
            } else {
                f.setId(idEditando);
                exito = dao.update(f);
            }

            if (exito) {
                JOptionPane.showMessageDialog(view, "Datos de faena procesados correctamente.");
                limpiarFormulario();
                cargarTabla();
            }
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(view, "Formato de Fecha incorrecto (Use: AAAA-MM-DD)");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, "Formato de Hora incorrecto (Use: HH:MM:SS)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error inesperado: " + ex.getMessage());
        }
    }

    private boolean validarCampos() {
        if (!Validator.isNotBlank(view.getTxtNombre().getText()) ||
                view.getCbEmbarcacion().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(view, "El nombre de la faena y la embarcación son obligatorios.");
            return false;
        }
        return true;
    }

    private void cargarParaEdicion() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione una faena de la lista.");
            return;
        }

        idEditando = (int) view.getTableModel().getValueAt(row, 0);
        Faena f = dao.readById(idEditando);

        if (f != null) {
            view.getTxtNombre().setText(f.getNombre());
            view.getTxtFecha().setText(df.format(f.getFecha()));
            view.getTxtHora().setText(f.getHoraEmbarco().toString());
            view.getTxtRuta().setText(f.getRuta());
            view.getCbEstado().setSelectedItem(f.getEstado());

            // Seleccionar el barco correspondiente en el JComboBox
            for (int i = 0; i < view.getCbEmbarcacion().getItemCount(); i++) {
                if (view.getCbEmbarcacion().getItemAt(i).getId() == f.getEmbarcacionId()) {
                    view.getCbEmbarcacion().setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void eliminar() {
        int row = view.getTable().getSelectedRow();
        if (row == -1)
            return;

        int id = (int) view.getTableModel().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Desea eliminar esta faena? Se perderá la tripulación asociada.", "Alerta", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION && dao.delete(id)) {
            cargarTabla();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        idEditando = -1;
        view.getTxtNombre().setText("");
        view.getTxtFecha().setText("");
        view.getTxtHora().setText("");
        view.getTxtRuta().setText("");
        view.getCbEstado().setSelectedIndex(0);
        view.getTable().clearSelection();
    }
}