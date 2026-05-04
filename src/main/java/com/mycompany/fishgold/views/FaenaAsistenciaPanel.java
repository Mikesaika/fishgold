package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.mycompany.fishgold.models.Planificacion;
import com.mycompany.fishgold.models.Trabajador;
import javax.swing.table.DefaultTableModel;

/**
 * FaenaAsistenciaPanel: Versión de Auditoría.
 * Centraliza la visualización de quiénes están asignados a cada viaje.
 */
public class FaenaAsistenciaPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Planificacion> cbPlanificacion;
    private JComboBox<Trabajador> cbTrabajador;
    private JTextField txtSearch;
    private JButton btnAdd, btnRefresh, btnClear;

    private JLabel errPlanificacion, errTrabajador;

    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_ACCENTO = new Color(37, 99, 235);
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105);
    private final Color COLOR_TEXTO = new Color(15, 23, 42);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Color COLOR_ERROR = new Color(220, 38, 38);

    public FaenaAsistenciaPanel() {
        setLayout(new BorderLayout(30, 0));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        initHeader();
        initCenterTable();
        initSidebar();
    }

    private void initHeader() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 25));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Historial de Asistencia");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(COLOR_TEXTO);

        JLabel lblSub = new JLabel("Consulte el personal asignado a las faenas activas y pasadas.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(COLOR_SECUNDARIO);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        searchBar.setOpaque(false);

        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(350, 40));
        txtSearch.putClientProperty("JTextField.placeholderText",
                "Filtrar por código de viaje o nombre de trabajador...");

        btnRefresh = createStyledButton("↺ Sincronizar", COLOR_ACCENTO);
        btnRefresh.setPreferredSize(new Dimension(160, 40));

        searchBar.add(txtSearch);
        searchBar.add(Box.createRigidArea(new Dimension(15, 0)));
        searchBar.add(btnRefresh);

        topPanel.add(titleGroup, BorderLayout.NORTH);
        topPanel.add(searchBar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initCenterTable() {
        String[] columns = { "ID", "Código Viaje", "Tripulante", "Fecha Registro" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // OCULTAR ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(380, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH; // CLAVE: Empuja todo hacia arriba
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int y = 0;

        JLabel lblForm = new JLabel("Asignación Manual");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblForm.setForeground(COLOR_ACCENTO);
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 25, 0);
        sidebar.add(lblForm, gbc);

        cbPlanificacion = new JComboBox<>();
        cbTrabajador = new JComboBox<>();
        styleComboBox(cbPlanificacion);
        styleComboBox(cbTrabajador);

        // --- VIAJE ---
        addLabeledField("VIAJE DESTINO", cbPlanificacion, sidebar, gbc, y);
        y += 2;
        errPlanificacion = addErrorLabel(sidebar, gbc, y++);

        // --- TRABAJADOR ---
        addLabeledField("TRABAJADOR A VINCULAR", cbTrabajador, sidebar, gbc, y);
        y += 2;
        errTrabajador = addErrorLabel(sidebar, gbc, y++);

        // Info de advertencia
        JLabel lblInfo = new JLabel(
                "<html><body style='width: 250px;'>Solo use este panel para correcciones manuales. La tripulación estándar se asigna en <b>Planificación</b>.</body></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(COLOR_SECUNDARIO);
        gbc.gridy = y++;
        gbc.insets = new Insets(10, 0, 25, 0);
        sidebar.add(lblInfo, gbc);

        // Botones
        JPanel btnActions = new JPanel(new GridLayout(1, 2, 12, 0));
        btnActions.setOpaque(false);
        btnAdd = createStyledButton("Asignar", COLOR_ACCENTO);
        btnClear = createStyledButton("Limpiar", COLOR_SECUNDARIO);
        btnActions.add(btnAdd);
        btnActions.add(btnClear);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(btnActions, gbc);

        // El Glue: Absorbe el espacio sobrante al final
        gbc.gridy = y++;
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);

        add(sidebar, BorderLayout.EAST);
    }

    private void addLabeledField(String labelText, JComponent field, JPanel panel, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(148, 163, 184));
        gbc.gridy = y;
        gbc.insets = new Insets(8, 0, 4, 0);
        panel.add(label, gbc);

        gbc.gridy = y + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(field, gbc);
    }

    private JLabel addErrorLabel(JPanel panel, GridBagConstraints gbc, int y) {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(COLOR_ERROR);
        gbc.gridy = y;
        gbc.insets = new Insets(2, 0, 15, 0); // Espacio controlado después del error
        panel.add(l, gbc);
        return l;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setBackground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(COLOR_ACCENTO);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    // --- GETTERS ---
    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JComboBox<Planificacion> getCbPlanificacion() {
        return cbPlanificacion;
    }

    public JComboBox<Trabajador> getCbTrabajador() {
        return cbTrabajador;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JButton getBtnClear() {
        return btnClear;
    }

    public JLabel getErrPlanificacion() {
        return errPlanificacion;
    }

    public JLabel getErrTrabajador() {
        return errTrabajador;
    }
}