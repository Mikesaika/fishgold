package com.mycompany.fishgold.views;

import com.mycompany.fishgold.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TripulacionPanel extends JPanel {
    private JComboBox<Faena> cbFaena;
    private JComboBox<Trabajador> cbCapitan, cbTrabajador;
    private JComboBox<Cargo> cbCargo;
    private JTextField txtDescripcion;
    private JButton btnAsignarCapitan, btnAsignarTrabajador, btnEliminar;
    private JTable table;
    private DefaultTableModel tableModel;

    public TripulacionPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));

        // 1. Selector de Faena (Prioridad visual)
        JPanel faenaSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        faenaSelectorPanel.setBorder(BorderFactory.createTitledBorder(" 1. Seleccione Faena de Pesca "));
        cbFaena = new JComboBox<>();
        cbFaena.setPreferredSize(new Dimension(400, 30));
        faenaSelectorPanel.add(new JLabel("Faena Destino:"));
        faenaSelectorPanel.add(cbFaena);

        northPanel.add(faenaSelectorPanel, BorderLayout.NORTH);

        // 2. Formulario de Asignación
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(" 2. Configuración de Equipo "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila Capitán
        addLabel(formPanel, "Capitán Autorizado:", 0, 0, gbc);
        cbCapitan = new JComboBox<>();
        formPanel.add(cbCapitan, getGBC(1, 0, gbc));

        btnAsignarCapitan = new JButton("Designar como Mando");
        btnAsignarCapitan.setBackground(new Color(52, 73, 94));
        btnAsignarCapitan.setForeground(Color.WHITE);
        formPanel.add(btnAsignarCapitan, getGBC(2, 0, gbc));

        // Fila Personal Operativo
        addLabel(formPanel, "Tripulante / Especialista:", 0, 1, gbc);
        cbTrabajador = new JComboBox<>();
        formPanel.add(cbTrabajador, getGBC(1, 1, gbc));

        addLabel(formPanel, "Rol a Desempeñar:", 0, 2, gbc);
        cbCargo = new JComboBox<>();
        formPanel.add(cbCargo, getGBC(1, 2, gbc));

        addLabel(formPanel, "Notas / Descripción:", 0, 3, gbc);
        txtDescripcion = new JTextField();
        formPanel.add(txtDescripcion, getGBC(1, 3, gbc));

        btnAsignarTrabajador = new JButton("Agregar a la Lista");
        formPanel.add(btnAsignarTrabajador, getGBC(2, 3, gbc));

        northPanel.add(formPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        // --- PANEL DE LISTADO (CENTRO) ---
        JPanel listPanel = new JPanel(new BorderLayout(0, 10));
        listPanel.setBorder(BorderFactory.createTitledBorder(" 3. Tripulación Asignada "));

        String[] cols = { "ID", "Nombre del Tripulante", "Cargo Asignado", "Observaciones" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        listPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnEliminar = new JButton("Retirar del Equipo");
        btnEliminar.setForeground(new Color(192, 57, 43)); // Rojo
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPnl.add(btnEliminar);
        listPanel.add(btnPnl, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.CENTER);
    }

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
        return c;
    }

    // --- GETTERS ---
    public JComboBox<Faena> getCbFaena() {
        return cbFaena;
    }

    public JComboBox<Trabajador> getCbCapitan() {
        return cbCapitan;
    }

    public JComboBox<Trabajador> getCbTrabajador() {
        return cbTrabajador;
    }

    public JComboBox<Cargo> getCbCargo() {
        return cbCargo;
    }

    public JTextField getTxtDescripcion() {
        return txtDescripcion;
    }

    public JButton getBtnAsignarCapitan() {
        return btnAsignarCapitan;
    }

    public JButton getBtnAsignarTrabajador() {
        return btnAsignarTrabajador;
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }
}