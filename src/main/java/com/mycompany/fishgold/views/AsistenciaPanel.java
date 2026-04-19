package com.mycompany.fishgold.views;

import com.mycompany.fishgold.models.Faena;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AsistenciaPanel extends JPanel {
    private JTabbedPane tabbedPane;

    // Componentes de Embarco
    private JComboBox<Faena> cbFaenaEmbarco;
    private JTable tableEmbarco;
    private DefaultTableModel modelEmbarco;
    private JButton btnMarcarEmbarco;

    // Componentes de Desembarco
    private JComboBox<Faena> cbFaenaDesembarco;
    private JTable tableDesembarco;
    private DefaultTableModel modelDesembarco;
    private JButton btnMarcarDesembarco;

    // Componentes de Reporte
    private JComboBox<Faena> cbFaenaReporte;
    private JTable tableReporte;
    private DefaultTableModel modelReporte;

    public AsistenciaPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Estilo visual del TabbedPane
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        tabbedPane.addTab("Embarco", createActionPanel("EMBARCO"));
        tabbedPane.addTab("Desembarco", createActionPanel("DESEMBARCO"));
        tabbedPane.addTab("Reporte General", createReportePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * MEJORA: Método unificado para crear paneles de Embarco y Desembarco.
     * Reduce la duplicidad de código.
     */
    private JPanel createActionPanel(String tipo) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel("Faena Activa:");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));

        JComboBox<Faena> cb = new JComboBox<>();
        if (tipo.equals("EMBARCO"))
            cbFaenaEmbarco = cb;
        else
            cbFaenaDesembarco = cb;

        topPanel.add(lbl);
        topPanel.add(cb);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = { "ID", "Tripulante", "Cargo", "Estado de Asistencia" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        if (tipo.equals("EMBARCO")) {
            modelEmbarco = model;
            tableEmbarco = table;
        } else {
            modelDesembarco = model;
            tableDesembarco = table;
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btn = new JButton("Alternar Estado de " + tipo);
        btn.setPreferredSize(new Dimension(200, 35));

        if (tipo.equals("EMBARCO"))
            btnMarcarEmbarco = btn;
        else
            btnMarcarDesembarco = btn;

        bottomPanel.add(btn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cbFaenaReporte = new JComboBox<>();
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Historial de Faenas:"));
        top.add(cbFaenaReporte);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = { "ID", "Trabajador", "Cargo", "Asist. Embarco", "Asist. Desembarco" };
        modelReporte = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableReporte = new JTable(modelReporte);
        tableReporte.setRowHeight(22);
        StatusCellRenderer renderer = new StatusCellRenderer();
        tableReporte.getColumnModel().getColumn(3).setCellRenderer(renderer);
        tableReporte.getColumnModel().getColumn(4).setCellRenderer(renderer);

        panel.add(new JScrollPane(tableReporte), BorderLayout.CENTER);
        return panel;
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String val = value.toString();
                if (val.equals("Presente")) {
                    c.setForeground(new Color(0, 128, 0)); // Verde
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (val.equals("Ausente")) {
                    c.setForeground(Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.GRAY); // No registrado
                }
            }

            if (isSelected)
                c.setBackground(table.getSelectionBackground());
            else
                c.setBackground(Color.WHITE);

            return c;
        }
    }

    // Getters...
    public JComboBox<Faena> getCbFaenaEmbarco() {
        return cbFaenaEmbarco;
    }

    public JTable getTableEmbarco() {
        return tableEmbarco;
    }

    public DefaultTableModel getModelEmbarco() {
        return modelEmbarco;
    }

    public JButton getBtnMarcarEmbarco() {
        return btnMarcarEmbarco;
    }

    public JComboBox<Faena> getCbFaenaDesembarco() {
        return cbFaenaDesembarco;
    }

    public JTable getTableDesembarco() {
        return tableDesembarco;
    }

    public DefaultTableModel getModelDesembarco() {
        return modelDesembarco;
    }

    public JButton getBtnMarcarDesembarco() {
        return btnMarcarDesembarco;
    }

    public JComboBox<Faena> getCbFaenaReporte() {
        return cbFaenaReporte;
    }

    public JTable getTableReporte() {
        return tableReporte;
    }

    public DefaultTableModel getModelReporte() {
        return modelReporte;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}