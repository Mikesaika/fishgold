package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.Trabajador;
import com.mycompany.fishgold.models.TrabajadorDAO;
import com.mycompany.fishgold.views.TrabajadorPanel;
import com.mycompany.fishgold.util.Validator;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TrabajadorController {
    private final TrabajadorPanel view;
    private final TrabajadorDAO dao;

    public TrabajadorController(TrabajadorPanel view, TrabajadorDAO dao) {
        this.view = view;
        this.dao = dao;
        init();
    }

    private void init() {
        // Listeners de botones
        view.getBtnAdd().addActionListener(e -> agregarTrabajador());
        view.getBtnUpdate().addActionListener(e -> actualizarTrabajador());
        view.getBtnDelete().addActionListener(e -> eliminarTrabajador());
        view.getBtnClear().addActionListener(e -> limpiarFormulario());

        // BUSQUEDA EN TIEMPO REAL: Reemplaza el listener del botón antiguo
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarTrabajador();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarTrabajador();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarTrabajador();
            }
        });

        // Sincronización Tabla -> Formulario
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        cargarTabla(dao.readAll());
    }

    public void recargarTabla() {
        cargarTabla(dao.readAll());
    }

    private void cargarTabla(List<Trabajador> lista) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        for (Trabajador t : lista) {
            model.addRow(new Object[] {
                    t.getId(), // Sigue en el modelo para lógica, pero oculto en vista
                    t.getCedulaDni(),
                    t.getNombreCompleto(),
                    t.getRolCargo(),
                    t.getTelefono(),
                    t.getDireccion(),
                    t.getEstado(),
                    t.getFechaRegistro()
            });
        }
    }

    private void agregarTrabajador() {
        Trabajador t = extraerDatosDeVista();
        if (t == null)
            return;

        if (dao.create(t)) {
            JOptionPane.showMessageDialog(view, "✅ Personal registrado correctamente.");
            limpiarFormulario();
            cargarTabla(dao.readAll());
        } else {
            JOptionPane.showMessageDialog(view, "Error: La cédula ya existe o fallo de conexión.",
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTrabajador() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un trabajador de la tabla.");
            return;
        }

        Trabajador t = extraerDatosDeVista();
        if (t == null)
            return;

        // Recuperar ID de la columna 0 (oculta)
        t.setId((int) view.getTable().getValueAt(fila, 0));

        if (dao.update(t)) {
            JOptionPane.showMessageDialog(view, "🔄 Datos actualizados.");
            limpiarFormulario();
            cargarTabla(dao.readAll());
        }
    }

    private void eliminarTrabajador() {
        int fila = view.getTable().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un trabajador.");
            return;
        }

        int id = (int) view.getTable().getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Eliminar este registro? Puede afectar el historial de faenas.", "Confirmar Baja",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(view, "🗑 Trabajador eliminado.");
                limpiarFormulario();
                cargarTabla(dao.readAll());
            }
        }
    }

    private void buscarTrabajador() {
        String query = view.getTxtSearch().getText().trim();
        cargarTabla(dao.search(query));
    }

    private void limpiarFormulario() {
        view.getTxtCedula().setText("");
        view.getTxtNombre().setText("");
        view.getCbRol().setSelectedIndex(0);
        view.getTxtTelefono().setText("");
        view.getTxtDireccion().setText("");
        view.getCbEstado().setSelectedIndex(0);
        view.getTable().clearSelection();

        // Limpiar mensajes de error
        view.getErrCedula().setText(" ");
        view.getErrNombre().setText(" ");
        view.getErrTelefono().setText(" ");

        resetBorders();
    }

    private void seleccionarFila() {
        int fila = view.getTable().getSelectedRow();
        if (fila != -1) {
            // Sincronizar datos usando los índices de columna (ID es 0)
            view.getTxtCedula().setText(view.getTable().getValueAt(fila, 1).toString());
            view.getTxtNombre().setText(view.getTable().getValueAt(fila, 2).toString());
            view.getCbRol().setSelectedItem(view.getTable().getValueAt(fila, 3).toString());
            view.getTxtTelefono().setText(
                    view.getTable().getValueAt(fila, 4) != null ? view.getTable().getValueAt(fila, 4).toString() : "");
            view.getTxtDireccion().setText(
                    view.getTable().getValueAt(fila, 5) != null ? view.getTable().getValueAt(fila, 5).toString() : "");
            view.getCbEstado().setSelectedItem(view.getTable().getValueAt(fila, 6).toString());
        }
    }

    private Trabajador extraerDatosDeVista() {
        // Reset de mensajes de error
        view.getErrCedula().setText(" ");
        view.getErrNombre().setText(" ");
        view.getErrTelefono().setText(" ");

        // Validaciones con feedback en los nuevos labels de error
        boolean v1 = Validator.isNotBlank(view.getTxtCedula());
        if (!v1)
            view.getErrCedula().setText("⚠ Ingrese una cédula válida");

        boolean v2 = Validator.isAlpha(view.getTxtNombre());
        if (!v2)
            view.getErrNombre().setText("⚠ Use solo letras");

        boolean v3 = Validator.isValidPhone(view.getTxtTelefono());
        if (!v3)
            view.getErrTelefono().setText("⚠ Formato de teléfono incorrecto");

        if (!(v1 && v2 && v3))
            return null;

        Trabajador t = new Trabajador();
        t.setCedulaDni(view.getTxtCedula().getText().trim());
        t.setNombreCompleto(view.getTxtNombre().getText().trim());
        t.setRolCargo(view.getCbRol().getSelectedItem().toString());
        t.setTelefono(view.getTxtTelefono().getText().trim());
        t.setDireccion(view.getTxtDireccion().getText().trim());
        t.setEstado(view.getCbEstado().getSelectedItem().toString());
        return t;
    }

    private void resetBorders() {
        Border defaultBorder = UIManager.getBorder("TextField.border");
        view.getTxtCedula().setBorder(defaultBorder);
        view.getTxtNombre().setBorder(defaultBorder);
        view.getTxtTelefono().setBorder(defaultBorder);
    }
}