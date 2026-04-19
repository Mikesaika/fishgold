package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrabajadorPanel extends JPanel {
    private JTextField txtNombre, txtDireccion, txtEmergNombre, txtEmergRelacion, txtEmergTelefono, txtOtroPuesto;
    private JRadioButton rbLicenciaSi, rbLicenciaNo;
    private JComboBox<String> cbPuestos, cbEstado;
    private JButton btnGuardar, btnLimpiar, btnEditar, btnEliminar;
    private JTable table;
    private DefaultTableModel tableModel;

    public TrabajadorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Ficha del Trabajador "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Datos Personales
        addLabel(formContainer, "Nombre Completo:", 0, 0, gbc);
        txtNombre = new JTextField(18);
        formContainer.add(txtNombre, getGBC(1, 0, gbc));

        addLabel(formContainer, "¿Licencia de Navegación?:", 2, 0, gbc);
        JPanel pnlRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rbLicenciaSi = new JRadioButton("Sí");
        rbLicenciaNo = new JRadioButton("No", true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbLicenciaSi);
        bg.add(rbLicenciaNo);
        pnlRadio.add(rbLicenciaSi);
        pnlRadio.add(rbLicenciaNo);
        formContainer.add(pnlRadio, getGBC(3, 0, gbc));

        // Fila 1: Ubicación y Estado
        addLabel(formContainer, "Dirección Domiciliaria:", 0, 1, gbc);
        txtDireccion = new JTextField(18);
        formContainer.add(txtDireccion, getGBC(1, 1, gbc));

        addLabel(formContainer, "Estado Laboral:", 2, 1, gbc);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        formContainer.add(cbEstado, getGBC(3, 1, gbc));

        // Fila 2: SECCIÓN EMERGENCIA (Visualmente agrupada)
        addLabel(formContainer, "Contacto Emergencia:", 0, 2, gbc);
        txtEmergNombre = new JTextField(18);
        txtEmergNombre.setToolTipText("Nombre del contacto");
        formContainer.add(txtEmergNombre, getGBC(1, 2, gbc));

        addLabel(formContainer, "Relación/Parentesco:", 2, 2, gbc);
        txtEmergRelacion = new JTextField(12);
        formContainer.add(txtEmergRelacion, getGBC(3, 2, gbc));

        // Fila 3: Teléfono y Cargos
        addLabel(formContainer, "Teléfono Emergencia:", 0, 3, gbc);
        txtEmergTelefono = new JTextField(18);
        formContainer.add(txtEmergTelefono, getGBC(1, 3, gbc));

        addLabel(formContainer, "Cargo de Experiencia:", 2, 3, gbc);
        cbPuestos = new JComboBox<>(new String[] { "Ninguno", "Capitán", "Oficial", "Cocinero", "Otro" });
        formContainer.add(cbPuestos, getGBC(3, 3, gbc));

        // Fila 4: Campo condicional
        addLabel(formContainer, "Especifique Puesto:", 2, 4, gbc);
        txtOtroPuesto = new JTextField(12);
        txtOtroPuesto.setEnabled(false);
        formContainer.add(txtOtroPuesto, getGBC(3, 4, gbc));

        // Botones Formulario
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Registrar Trabajador");
        btnLimpiar = new JButton("Limpiar Campos");
        btnPnl.add(btnGuardar);
        btnPnl.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        formContainer.add(btnPnl, gbc);

        add(formContainer, BorderLayout.NORTH);

        // --- TABLA (CENTRO) ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setBorder(BorderFactory.createTitledBorder(" Nómina de Personal "));

        String[] cols = { "ID", "Nombre", "Licencia", "Dirección", "Contacto", "Teléfono", "Cargo", "Estado" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEditar = new JButton("Cargar Selección");
        btnEliminar = new JButton("Dar de Baja");
        btnEliminar.setForeground(new Color(180, 0, 0));
        tableActions.add(btnEditar);
        tableActions.add(btnEliminar);
        tableContainer.add(tableActions, BorderLayout.SOUTH);

        add(tableContainer, BorderLayout.CENTER);
    }

    // Auxiliares
    private void addLabel(JPanel p, String text, int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, c);
    }

    private GridBagConstraints getGBC(int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        return c;
    }

    // Getters / Setters especializados
    public boolean isLicencia() {
        return rbLicenciaSi.isSelected();
    }

    public void setLicencia(boolean has) {
        if (has)
            rbLicenciaSi.setSelected(true);
        else
            rbLicenciaNo.setSelected(true);
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtDireccion() {
        return txtDireccion;
    }

    public JTextField getTxtEmergNombre() {
        return txtEmergNombre;
    }

    public JTextField getTxtEmergRelacion() {
        return txtEmergRelacion;
    }

    public JTextField getTxtEmergTelefono() {
        return txtEmergTelefono;
    }

    public JComboBox<String> getCbPuestos() {
        return cbPuestos;
    }

    public JTextField getTxtOtroPuesto() {
        return txtOtroPuesto;
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