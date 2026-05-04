package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.mycompany.fishgold.models.Planificacion;

public class LiquidacionCapturaPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Planificacion> cbPlanificacion;
    private JTextField txtPesoPescado, txtCapitan, txtSearch;
    private JTextArea txtObservaciones;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnReporte;

    // COMPONENTE EXPERTO: Label para previsualizar el cálculo en tiempo real
    private JLabel lblPreviewCalculo;
    private JLabel errPlanificacion, errPeso, errCapitan;

    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_ACCENTO = new Color(37, 99, 235);
    private final Color COLOR_EXITO = new Color(22, 163, 74);
    private final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105);
    private final Color COLOR_DARK = new Color(15, 23, 42);

    public LiquidacionCapturaPanel() {
        setLayout(new BorderLayout(30, 0));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        initTopSection();
        initTableSection();
        initFormSidebar();
    }

    private void initTopSection() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 25));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Liquidación y Pesca");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(COLOR_DARK);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);

        txtSearch = new JTextField(22);
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.putClientProperty("JTextField.placeholderText", "Escriba el código del viaje o capitán...");

        btnReporte = createStyledButton("📄 Exportar Reporte PDF", new Color(51, 65, 85));

        actionRow.add(txtSearch, BorderLayout.WEST);
        actionRow.add(btnReporte, BorderLayout.EAST);

        topPanel.add(actionRow, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTableSection() {
        // CORRECCIÓN: La tabla ahora es la evidencia del cálculo procesado
        String[] columns = { "ID", "Planificación", "Captura (Kg)", "Pago Total ($)", "Responsable", "Fecha", "Notas" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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
    }

    private void initFormSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(400, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int y = 0;

        JLabel lblFormTitle = new JLabel("CIERRE DE FAENA");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(COLOR_ACCENTO);
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 20, 0);
        sidebar.add(lblFormTitle, gbc);

        cbPlanificacion = new JComboBox<>();
        txtPesoPescado = new JTextField();
        txtCapitan = new JTextField();
        txtObservaciones = new JTextArea(3, 15);
        txtObservaciones.setLineWrap(true);

        // --- CAMPOS CON FEEDBACK ---
        addLabeledField("VIAJE A FINALIZAR", cbPlanificacion, sidebar, gbc, y++);
        errPlanificacion = addErrorLabel(sidebar, gbc, y++);

        addLabeledField("PESO REAL CAPTURADO (KG)", txtPesoPescado, sidebar, gbc, y++);
        errPeso = addErrorLabel(sidebar, gbc, y++);

        // PANEL DE CÁLCULO EN TIEMPO REAL
        JPanel calculoPanel = new JPanel(new BorderLayout());
        calculoPanel.setBackground(new Color(248, 250, 252));
        calculoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)));

        lblPreviewCalculo = new JLabel("$ 0.00");
        lblPreviewCalculo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblPreviewCalculo.setForeground(COLOR_EXITO);
        lblPreviewCalculo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblInfo = new JLabel("MONTO ESTIMADO A PAGAR");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblInfo.setForeground(COLOR_SECUNDARIO);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

        calculoPanel.add(lblInfo, BorderLayout.NORTH);
        calculoPanel.add(lblPreviewCalculo, BorderLayout.CENTER);

        gbc.gridy = y++;
        gbc.insets = new Insets(10, 0, 20, 0);
        sidebar.add(calculoPanel, gbc);

        addLabeledField("CAPITÁN RESPONSABLE", txtCapitan, sidebar, gbc, y++);
        errCapitan = addErrorLabel(sidebar, gbc, y++);

        addLabeledField("OBSERVACIONES", new JScrollPane(txtObservaciones), sidebar, gbc, y++);
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 20, 0);
        sidebar.add(Box.createVerticalStrut(5), gbc);

        // Botonera
        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);
        btnAdd = createStyledButton("✓ Finalizar y Pagar", COLOR_ACCENTO);
        btnUpdate = createStyledButton("✎ Editar", COLOR_EXITO);
        btnDelete = createStyledButton("🗑 Borrar", COLOR_PELIGRO);
        btnClear = createStyledButton("⎙ Limpiar", COLOR_SECUNDARIO);

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        gbc.gridy = y++;
        sidebar.add(actions, gbc);

        gbc.weighty = 1.0;
        gbc.gridy = y++;
        sidebar.add(Box.createVerticalGlue(), gbc);

        add(sidebar, BorderLayout.EAST);
    }

    private void addLabeledField(String labelText, JComponent field, JPanel panel, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(COLOR_SECUNDARIO);
        gbc.gridy = y;
        gbc.insets = new Insets(8, 0, 4, 0);
        panel.add(label, gbc);
        field.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = y + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(field, gbc);
    }

    private JLabel addErrorLabel(JPanel panel, GridBagConstraints gbc, int y) {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(COLOR_PELIGRO);
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(l, gbc);
        return l;
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
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    // --- GETTERS ---
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

    public JButton getBtnReporte() {
        return btnReporte;
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JComboBox<Planificacion> getCbPlanificacion() {
        return cbPlanificacion;
    }

    public JTextField getTxtPesoPescado() {
        return txtPesoPescado;
    }

    public JTextField getTxtCapitan() {
        return txtCapitan;
    }

    public JTextArea getTxtObservaciones() {
        return txtObservaciones;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JLabel getLblPreviewCalculo() {
        return lblPreviewCalculo;
    }

    public JLabel getErrPeso() {
        return errPeso;
    }

    public JLabel getErrCapitan() {
        return errCapitan;
    }
}