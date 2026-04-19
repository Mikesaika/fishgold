package com.mycompany.fishgold.views;

import com.mycompany.fishgold.models.Embarcacion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel para la programación y gestión de faenas de pesca.
 * Implementa una disposición organizada mediante GridBagLayout.
 */
public class FaenaPanel extends JPanel {
    private JTextField txtNombre, txtFecha, txtHora, txtRuta;
    private JComboBox<Embarcacion> cbEmbarcacion;
    private JComboBox<String> cbEstado;
    private JButton btnGuardar, btnLimpiar, btnEditar, btnEliminar;
    private JTable table;
    private DefaultTableModel tableModel;

    public FaenaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // --- SECCIÓN NORTE: FORMULARIO DE REGISTRO ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Programación de Nueva Faena "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0
        addLabel(formContainer, "Nombre de Faena:", 0, 0, gbc);
        txtNombre = new JTextField(15);
        formContainer.add(txtNombre, getGBC(1, 0, gbc));

        addLabel(formContainer, "Fecha (AAAA-MM-DD):", 2, 0, gbc);
        txtFecha = new JTextField(10);
        formContainer.add(txtFecha, getGBC(3, 0, gbc));

        // Fila 1
        addLabel(formContainer, "Hora Embarco (HH:MM):", 0, 1, gbc);
        txtHora = new JTextField(10);
        formContainer.add(txtHora, getGBC(1, 1, gbc));

        addLabel(formContainer, "Embarcación Asignada:", 2, 1, gbc);
        cbEmbarcacion = new JComboBox<>();
        formContainer.add(cbEmbarcacion, getGBC(3, 1, gbc));

        // Fila 2
        addLabel(formContainer, "Ruta / Zona de Pesca:", 0, 2, gbc);
        txtRuta = new JTextField(15);
        formContainer.add(txtRuta, getGBC(1, 2, gbc));

        addLabel(formContainer, "Estado de Faena:", 2, 2, gbc);
        cbEstado = new JComboBox<>(new String[] { "Pendiente", "En Curso", "Finalizada" });
        formContainer.add(cbEstado, getGBC(3, 2, gbc));

        // Panel de Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnGuardar = new JButton("Guardar Faena");
        btnLimpiar = new JButton("Limpiar Formulario");
        btnPanel.add(btnGuardar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 0, 5, 0);
        formContainer.add(btnPanel, gbc);

        add(formContainer, BorderLayout.NORTH);

        // --- SECCIÓN CENTRAL: TABLA DE REGISTROS ---
        JPanel listContainer = new JPanel(new BorderLayout(0, 10));
        listContainer.setBorder(BorderFactory.createTitledBorder(" Historial de Faenas "));

        String[] cols = { "ID", "Nombre", "Fecha", "Hora", "Embarcación", "Ruta", "Estado" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        listContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        // Botones de acción de tabla
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEditar = new JButton("Cargar Datos");
        btnEliminar = new JButton("Eliminar Faena");
        btnEliminar.setForeground(new Color(180, 0, 0));

        tableActions.add(btnEditar);
        tableActions.add(btnEliminar);
        listContainer.add(tableActions, BorderLayout.SOUTH);

        add(listContainer, BorderLayout.CENTER);
    }

    // Auxiliares para GridBagLayout
    private void addLabel(JPanel p, String txt, int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        JLabel lbl = new JLabel(txt);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, c);
    }

    private GridBagConstraints getGBC(int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        return c;
    }

    // Getters
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtFecha() {
        return txtFecha;
    }

    public JTextField getTxtHora() {
        return txtHora;
    }

    public JComboBox<Embarcacion> getCbEmbarcacion() {
        return cbEmbarcacion;
    }

    public JTextField getTxtRuta() {
        return txtRuta;
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