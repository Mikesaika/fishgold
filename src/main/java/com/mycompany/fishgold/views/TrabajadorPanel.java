package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TrabajadorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCedula, txtNombre, txtTelefono, txtDireccion, txtSearch;
    private JComboBox<String> cbRol, cbEstado;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // Etiquetas de Error para validación en tiempo real
    private JLabel errCedula, errNombre, errTelefono, errCargo, errDireccion, errEstado;

    // Paleta de Colores Slate & Blue (Profesional)
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_ACCENTO = new Color(37, 99, 235);
    private final Color COLOR_EXITO = new Color(22, 163, 74);
    private final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105);

    public TrabajadorPanel() {
        setLayout(new BorderLayout(30, 0));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        initTopPanel();
        initTablePanel();
        initFormSidebar();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Gestión de Personal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(15, 23, 42));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);

        // Buscador en tiempo real
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(350, 40));
        txtSearch.putClientProperty("JTextField.placeholderText", "Escriba nombre, cédula o cargo para filtrar...");

        actionRow.add(txtSearch, BorderLayout.WEST);

        topPanel.add(actionRow, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTablePanel() {
        String[] columns = { "ID", "Cédula", "Nombre", "Rol", "Teléfono", "Dirección", "Estado", "Registro" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // CORRECCIÓN: OCULTAR COLUMNA ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initFormSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(380, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblFormTitle = new JLabel("DATOS DEL TRABAJADOR");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(COLOR_ACCENTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        sidebar.add(lblFormTitle, gbc);

        // Inputs
        txtCedula = new JTextField();
        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtDireccion = new JTextField();
        cbRol = new JComboBox<>(new String[] { "seleccione...","Capitán", "Motorista", "Pescador", "Cocinero", "Ayudante" });
        cbRol.setBackground(Color.WHITE);
        cbEstado = new JComboBox<>(new String[] { "seleccione...","Activo", "Inactivo" });
        cbEstado.setBackground(Color.WHITE);

        // Añadir campos con labels y errores
        addLabeledField("CÉDULA / DNI", txtCedula, sidebar, gbc, 1);
        errCedula = createErrorLabel();
        addErrorLabel(errCedula, sidebar, gbc, 3);

        addLabeledField("NOMBRE COMPLETO", txtNombre, sidebar, gbc, 4);
        errNombre = createErrorLabel();
        addErrorLabel(errNombre, sidebar, gbc, 6);

        addLabeledField("CARGO OPERATIVO", cbRol, sidebar, gbc, 7);
        errCargo = createErrorLabel();
        addErrorLabel(errCargo, sidebar, gbc, 9);

        addLabeledField("TELÉFONO DE CONTACTO", txtTelefono, sidebar, gbc, 10);
        errTelefono = createErrorLabel();
        addErrorLabel(errTelefono, sidebar, gbc, 12);

        addLabeledField("DIRECCIÓN DE DOMICILIO", txtDireccion, sidebar, gbc, 13);
        errDireccion = createErrorLabel();
        addErrorLabel(errDireccion, sidebar, gbc, 15);

        addLabeledField("ESTADO EN SISTEMA", cbEstado, sidebar, gbc, 16);
        errEstado = createErrorLabel();
        addErrorLabel(errEstado, sidebar, gbc, 18);

        // Botonera
        JPanel actions = new JPanel(new GridLayout(2, 2, 12, 12));
        actions.setOpaque(false);
        btnAdd = createStyledButton("Guardar", COLOR_ACCENTO);
        btnUpdate = createStyledButton("Actualizar", COLOR_EXITO);
        btnDelete = createStyledButton("Eliminar", COLOR_PELIGRO);
        btnClear = createStyledButton("Limpiar", COLOR_SECUNDARIO);

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        gbc.gridy = 19;
        gbc.insets = new Insets(20, 0, 0, 0);
        sidebar.add(actions, gbc);

        gbc.gridy = 20;
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);

        add(sidebar, BorderLayout.EAST);
    }

    private void addLabeledField(String labelText, JComponent field, JPanel panel, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(100, 116, 139));
        gbc.gridy = y;
        gbc.insets = new Insets(8, 0, 4, 0);
        panel.add(label, gbc);

        field.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = y + 1;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(field, gbc);
    }

    private JLabel createErrorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(COLOR_PELIGRO);
        return l;
    }

    private void addErrorLabel(JLabel l, JPanel panel, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(l, gbc);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(COLOR_ACCENTO);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    // Getters actualizados
    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtCedula() {
        return txtCedula;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JComboBox<String> getCbRol() {
        return cbRol;
    }

    public JTextField getTxtTelefono() {
        return txtTelefono;
    }

    public JTextField getTxtDireccion() {
        return txtDireccion;
    }

    public JComboBox<String> getCbEstado() {
        return cbEstado;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnClear() {
        return btnClear;
    }

    // Getters para etiquetas de error
    public JLabel getErrCedula() {
        return errCedula;
    }

    public JLabel getErrNombre() {
        return errNombre;
    }

    public JLabel getErrTelefono() {
        return errTelefono;
    }

    public JLabel getErrCargo() {
        return errCargo;
    }

    public JLabel getErrDireccion() {
        return errDireccion;
    }

    public JLabel getErrEstado() {
        return errEstado;
    }
}