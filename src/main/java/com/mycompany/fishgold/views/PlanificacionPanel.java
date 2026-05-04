package com.mycompany.fishgold.views;

import com.mycompany.fishgold.models.Trabajador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlanificacionPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCodigo, txtEmbarcacion, txtDestino, txtMetaPeso, txtSearch;
    private JSpinner spinFechaSalida;
    private JComboBox<String> cbEstado;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnReporte;

    private JList<Trabajador> listTrabajadores;
    private DefaultListModel<Trabajador> listModelTrabajadores;

    private JLabel errCodigo, errEmbarcacion, errMeta, errTripulacion;

    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_ACCENTO = new Color(37, 99, 235);
    private final Color COLOR_EXITO = new Color(22, 163, 74);
    private final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private final Color COLOR_SECUNDARIO = new Color(100, 116, 139);

    public PlanificacionPanel() {
        setLayout(new BorderLayout(30, 0));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(40, 40, 40, 40));
        initComponents();
    }

    private void initComponents() {
        // --- NORTE: TÍTULO Y BÚSQUEDA ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 25));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Planificación de Viajes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(15, 23, 42));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(350, 40));
        txtSearch.putClientProperty("JTextField.placeholderText", "Buscar por código o embarcación...");
        btnReporte = createStyledButton("📄 Reporte de Flota", new Color(51, 65, 85));

        actionRow.add(txtSearch, BorderLayout.WEST);
        actionRow.add(btnReporte, BorderLayout.EAST);
        topPanel.add(actionRow, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTRO: TABLA ---
        String[] columns = { "ID", "Código", "Barco", "Destino", "Salida", "Meta (Kg)", "Estado" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        add(scrollPane, BorderLayout.CENTER);

        // --- ESTE: FORMULARIO SIDEBAR ---
        add(createFormSidebar(), BorderLayout.EAST);
    }

    private JPanel createFormSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(420, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int y = 0;

        JLabel lblFormTitle = new JLabel("CONFIGURAR VIAJE");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_ACCENTO);
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridy = y++;
        sidebar.add(lblFormTitle, gbc);

        // Instanciar campos
        txtCodigo = new JTextField();
        txtEmbarcacion = new JTextField();
        txtDestino = new JTextField();
        txtMetaPeso = new JTextField();
        cbEstado = new JComboBox<>(new String[] { "Pendiente", "En Curso", "Finalizado" });
        cbEstado.setBackground(Color.WHITE);
        spinFechaSalida = new JSpinner(new SpinnerDateModel());
        spinFechaSalida.setEditor(new JSpinner.DateEditor(spinFechaSalida, "yyyy-MM-dd"));

        addLabeledField("CÓDIGO DE VIAJE", txtCodigo, sidebar, gbc, y++);
        errCodigo = addErrorLabel(sidebar, gbc, y++);

        addLabeledField("EMBARCACIÓN", txtEmbarcacion, sidebar, gbc, y++);
        errEmbarcacion = addErrorLabel(sidebar, gbc, y++);

        addLabeledField("DESTINO", txtDestino, sidebar, gbc, y++);
        y++;

        addLabeledField("FECHA SALIDA", spinFechaSalida, sidebar, gbc, y++);
        y++;

        addLabeledField("META DE PESCA (KG)", txtMetaPeso, sidebar, gbc, y++);
        errMeta = addErrorLabel(sidebar, gbc, y++);

        // TRIPULACIÓN (FORZAR TAMAÑO)
        JLabel lblTrip = new JLabel("SELECCIONAR TRIPULACIÓN");
        lblTrip.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTrip.setForeground(COLOR_SECUNDARIO);
        gbc.gridy = y++;
        gbc.insets = new Insets(5, 0, 5, 0);
        sidebar.add(lblTrip, gbc);

        listModelTrabajadores = new DefaultListModel<>();
        listTrabajadores = new JList<>(listModelTrabajadores);
        listTrabajadores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane listScroll = new JScrollPane(listTrabajadores);
        // FORZADO DE TAMAÑO PARA GRIDBAGLAYOUT
        Dimension listDim = new Dimension(0, 160);
        listScroll.setPreferredSize(listDim);
        listScroll.setMinimumSize(new Dimension(100, 160));

        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.BOTH; // Permitir expansión para respetar el alto
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(listScroll, gbc);

        // Volvemos a horizontal para el label de error
        gbc.fill = GridBagConstraints.HORIZONTAL;
        errTripulacion = addErrorLabel(sidebar, gbc, y++);

        addLabeledField("ESTADO DEL VIAJE", cbEstado, sidebar, gbc, y++);
        y++;

        // Botonera
        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        btnAdd = createStyledButton("✓ Registrar", COLOR_ACCENTO);
        btnUpdate = createStyledButton("✎ Editar", COLOR_EXITO);
        btnDelete = createStyledButton("🗑 Borrar", COLOR_PELIGRO);
        btnClear = createStyledButton("⎙ Limpiar", new Color(71, 85, 105));

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        gbc.gridy = y++;
        gbc.insets = new Insets(10, 0, 0, 0);
        sidebar.add(actions, gbc);

        // Glue final
        gbc.gridy = y++;
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);

        return sidebar;
    }

    private void addLabeledField(String labelText, JComponent field, JPanel panel, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(COLOR_SECUNDARIO);
        gbc.gridy = y;
        gbc.insets = new Insets(5, 0, 3, 0);
        panel.add(label, gbc);
        field.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = y + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(field, gbc);
    }

    private JLabel addErrorLabel(JPanel panel, GridBagConstraints gbc, int y) {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(COLOR_PELIGRO);
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(l, gbc);
        return l;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(COLOR_ACCENTO);
        table.setShowVerticalLines(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    // --- GETTERS ---
    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtCodigo() {
        return txtCodigo;
    }

    public JTextField getTxtEmbarcacion() {
        return txtEmbarcacion;
    }

    public JTextField getTxtDestino() {
        return txtDestino;
    }

    public JTextField getTxtMetaPeso() {
        return txtMetaPeso;
    }

    public JList<Trabajador> getListTrabajadores() {
        return listTrabajadores;
    }

    public DefaultListModel<Trabajador> getListModelTrabajadores() {
        return listModelTrabajadores;
    }

    public JSpinner getSpinFechaSalida() {
        return spinFechaSalida;
    }

    public JComboBox<String> getCbEstado() {
        return cbEstado;
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

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JLabel getErrCodigo() {
        return errCodigo;
    }

    public JLabel getErrEmbarcacion() {
        return errEmbarcacion;
    }

    public JLabel getErrMeta() {
        return errMeta;
    }

    public JLabel getErrTripulacion() {
        return errTripulacion;
    }
}