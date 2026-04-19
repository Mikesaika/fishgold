package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel para la gestión de flota.
 * Utiliza GridBagLayout para un formulario alineado y profesional.
 */
public class EmbarcacionPanel extends JPanel {
    private JTextField txtNombre, txtPropietario, txtModelo, txtCapacidad, txtAnioCompra, txtMatricula;
    private JComboBox<String> cbEstado;
    private JButton btnGuardar, btnLimpiar, btnEditar, btnEliminar;
    private JTable table;
    private DefaultTableModel tableModel;

    public EmbarcacionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // --- SECCIÓN NORTE: FORMULARIO ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Datos de la Embarcación "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0
        addLabel(formContainer, "Nombre del Barco:", 0, 0, gbc);
        txtNombre = new JTextField(15);
        addWrappedComponent(formContainer, txtNombre, 1, 0, gbc);

        addLabel(formContainer, "Propietario:", 2, 0, gbc);
        txtPropietario = new JTextField(15);
        addWrappedComponent(formContainer, txtPropietario, 3, 0, gbc);

        // Fila 1
        addLabel(formContainer, "Modelo / Tipo:", 0, 1, gbc);
        txtModelo = new JTextField(15);
        addWrappedComponent(formContainer, txtModelo, 1, 1, gbc);

        addLabel(formContainer, "Capacidad (TN):", 2, 1, gbc);
        txtCapacidad = new JTextField(15);
        addWrappedComponent(formContainer, txtCapacidad, 3, 1, gbc);

        // Fila 2
        addLabel(formContainer, "Año de Compra:", 0, 2, gbc);
        txtAnioCompra = new JTextField(15);
        addWrappedComponent(formContainer, txtAnioCompra, 1, 2, gbc);

        addLabel(formContainer, "Matrícula Única:", 2, 2, gbc);
        txtMatricula = new JTextField(15);
        addWrappedComponent(formContainer, txtMatricula, 3, 2, gbc);

        // Fila 3
        addLabel(formContainer, "Estado Actual:", 0, 3, gbc);
        cbEstado = new JComboBox<>(new String[] { "Activa", "Reparación", "Inactiva" });
        addWrappedComponent(formContainer, cbEstado, 1, 3, gbc);

        // Panel de Botones del Formulario
        JPanel actionBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnGuardar = new JButton("Guardar Embarcación");
        btnLimpiar = new JButton("Nuevo / Limpiar");
        actionBtnPanel.add(btnGuardar);
        actionBtnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 0, 10, 0);
        formContainer.add(actionBtnPanel, gbc);

        add(formContainer, BorderLayout.NORTH);

        // --- SECCIÓN CENTRAL: TABLA ---
        JPanel listContainer = new JPanel(new BorderLayout(0, 10));
        listContainer.setBorder(BorderFactory.createTitledBorder(" Flota Registrada "));

        String[] cols = { "ID", "Nombre", "Dueño", "Modelo", "Cap.", "Año", "Matrícula", "Estado" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        listContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        // Botones de la Tabla
        JPanel tableBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEditar = new JButton("Cargar para Editar");
        btnEliminar = new JButton("Eliminar Registro");
        btnEliminar.setForeground(new Color(150, 0, 0));

        tableBtnPanel.add(btnEditar);
        tableBtnPanel.add(btnEliminar);
        listContainer.add(tableBtnPanel, BorderLayout.SOUTH);

        add(listContainer, BorderLayout.CENTER);
    }

    // Métodos utilitarios para simplificar el GridBagLayout
    private void addLabel(JPanel panel, String text, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(new JLabel(text), gbc);
    }

    private void addWrappedComponent(JPanel panel, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(comp, gbc);
    }

    // Getters...
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtPropietario() {
        return txtPropietario;
    }

    public JTextField getTxtModelo() {
        return txtModelo;
    }

    public JTextField getTxtCapacidad() {
        return txtCapacidad;
    }

    public JTextField getTxtAnioCompra() {
        return txtAnioCompra;
    }

    public JTextField getTxtMatricula() {
        return txtMatricula;
    }

    public JComboBox<String> getCbEstado() {
        return cbEstado;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }
}