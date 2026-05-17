package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mycompany.fishgold.models.Planificacion;
import com.mycompany.fishgold.models.Trabajador;

/**
 * Asistencia: lista clara de tripulantes con botones por fila y guardado conjunto.
 */
public class FaenaAsistenciaPanel extends JPanel {

    public static final Color COLOR_PRESENTE_BG = new Color(220, 252, 231);
    public static final Color COLOR_AUSENTE_BG = new Color(254, 226, 226);
    public static final Color COLOR_JUSTIFICADO_BG = new Color(254, 243, 199);
    public static final Color COLOR_NEUTRO_BG = new Color(248, 250, 252);

    public static class TripulanteFila extends JPanel {
        private final Trabajador trabajador;
        private String estadoEnBd;
        private String estadoElegido;
        private final JLabel lblNombre;
        private final JLabel lblRol;
        private final JLabel lblEstado;
        private final JButton btnPresente;
        private final JButton btnAusente;
        private final JButton btnJustificado;

        public TripulanteFila(Trabajador t, String estadoGuardado) {
            this.trabajador = t;
            this.estadoEnBd = estadoGuardado != null ? estadoGuardado : "Presente";
            this.estadoElegido = null;

            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(12, 14, 12, 14)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JPanel izq = new JPanel();
            izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
            izq.setOpaque(false);
            lblNombre = new JLabel(t.getNombreCompleto());
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblNombre.setForeground(new Color(44, 62, 80));
            lblRol = new JLabel(t.getRolCargo() != null ? t.getRolCargo() : "—");
            lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblRol.setForeground(new Color(100, 116, 139));
            lblEstado = new JLabel("Registrado: " + estadoEnBd);
            lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblEstado.setForeground(new Color(52, 73, 94));
            izq.add(lblNombre);
            izq.add(lblRol);
            izq.add(Box.createRigidArea(new Dimension(0, 4)));
            izq.add(lblEstado);

            JPanel botones = new JPanel(new GridLayout(1, 3, 8, 0));
            botones.setOpaque(false);
            btnPresente = mkBtn("Presente", new Color(22, 163, 74));
            btnAusente = mkBtn("Ausente", new Color(220, 38, 38));
            btnJustificado = mkBtn("Justificado", new Color(217, 119, 6));
            botones.add(btnPresente);
            botones.add(btnAusente);
            botones.add(btnJustificado);

            btnPresente.addActionListener(e -> aplicarEleccion("Presente"));
            btnAusente.addActionListener(e -> aplicarEleccion("Ausente"));
            btnJustificado.addActionListener(e -> aplicarEleccion("Justificado"));

            add(izq, BorderLayout.CENTER);
            add(botones, BorderLayout.EAST);

            pintarFondoSegunEstado(estadoEnBd);
        }

        private JButton mkBtn(String text, Color bg) {
            JButton b = new JButton(text);
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        private void aplicarEleccion(String estado) {
            estadoElegido = estado;
            lblEstado.setText("Marcar como: " + estado + " (use «Guardar todo»)");
            pintarFondoSegunEstado(estado);
        }

        private void pintarFondoSegunEstado(String estado) {
            Color c = COLOR_NEUTRO_BG;
            if ("Presente".equals(estado)) {
                c = COLOR_PRESENTE_BG;
            } else if ("Ausente".equals(estado)) {
                c = COLOR_AUSENTE_BG;
            } else if ("Justificado".equals(estado)) {
                c = COLOR_JUSTIFICADO_BG;
            }
            setBackground(c);
            for (Component comp : getComponents()) {
                if (comp instanceof JPanel) {
                    comp.setBackground(c);
                    for (Component c2 : ((JPanel) comp).getComponents()) {
                        if (c2 instanceof JPanel) {
                            c2.setBackground(c);
                        }
                    }
                }
            }
        }

        public Trabajador getTrabajador() {
            return trabajador;
        }

        /** Valor que debe persistirse (cambio del usuario o el ya guardado). */
        public String getEstadoParaGuardar() {
            return estadoElegido != null ? estadoElegido : estadoEnBd;
        }

        public boolean huboCambio() {
            return estadoElegido != null && !estadoElegido.equals(estadoEnBd);
        }

        public void refrescarDesdeBd(String nuevoEstado) {
            estadoEnBd = nuevoEstado;
            estadoElegido = null;
            lblEstado.setText("Registrado: " + estadoEnBd);
            pintarFondoSegunEstado(estadoEnBd);
        }
    }

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Planificacion> cbPlanificacion;
    private JTextField txtSearch;
    private JPanel tripulantesContainer;
    private final List<TripulanteFila> filasTripulantes = new ArrayList<>();
    private JButton btnGuardarTodo;
    private JButton btnRefresh;
    private JButton btnEliminar;
    private JLabel errPlanificacion;

    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_ACCENTO = new Color(52, 152, 219);
    private final Color COLOR_TEXTO = new Color(44, 62, 80);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Color COLOR_ERROR = new Color(220, 38, 38);

    public FaenaAsistenciaPanel() {
        setLayout(new BorderLayout(20, 16));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        initHeader();
        initCentroTripulantes();
        initTablaSur();
    }

    private void initHeader() {
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);

        JLabel lblTitle = new JLabel("Control de asistencia");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXTO);
        JLabel lblSub = new JLabel("Elija el viaje activo, marque a cada tripulante y pulse «Guardar todo».");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));
        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        texts.add(lblTitle);
        texts.add(lblSub);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);
        JLabel lViaje = new JLabel("Viaje:");
        lViaje.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cbPlanificacion = new JComboBox<>();
        cbPlanificacion.setPreferredSize(new Dimension(420, 38));
        btnGuardarTodo = new JButton("Guardar todo");
        btnGuardarTodo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardarTodo.setBackground(new Color(39, 174, 96));
        btnGuardarTodo.setForeground(Color.WHITE);
        btnGuardarTodo.setOpaque(true);
        btnGuardarTodo.setBorderPainted(false);
        btnRefresh = new JButton("Actualizar");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBackground(COLOR_ACCENTO);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setOpaque(true);
        btnRefresh.setBorderPainted(false);
        btnEliminar = new JButton("Eliminar registro");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEliminar.setBackground(COLOR_ERROR);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.add(lViaje);
        row.add(cbPlanificacion);
        row.add(btnGuardarTodo);
        row.add(btnRefresh);
        row.add(btnEliminar);

        errPlanificacion = new JLabel(" ");
        errPlanificacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errPlanificacion.setForeground(COLOR_ERROR);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchRow.setOpaque(false);
        txtSearch = new JTextField(28);
        txtSearch.setPreferredSize(new Dimension(320, 36));
        txtSearch.putClientProperty("JTextField.placeholderText", "Filtrar historial por viaje o nombre...");
        searchRow.add(new JLabel("Historial:"));
        searchRow.add(txtSearch);

        JPanel northStack = new JPanel();
        northStack.setLayout(new BoxLayout(northStack, BoxLayout.Y_AXIS));
        northStack.setOpaque(false);
        northStack.add(texts);
        northStack.add(Box.createRigidArea(new Dimension(0, 16)));
        northStack.add(row);
        northStack.add(errPlanificacion);
        northStack.add(Box.createRigidArea(new Dimension(0, 8)));
        northStack.add(searchRow);

        top.add(northStack, BorderLayout.NORTH);
        add(top, BorderLayout.NORTH);
    }

    private void initCentroTripulantes() {
        tripulantesContainer = new JPanel();
        tripulantesContainer.setLayout(new BoxLayout(tripulantesContainer, BoxLayout.Y_AXIS));
        tripulantesContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tripulantesContainer);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
                        "Tripulantes del viaje seleccionado",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        COLOR_TEXTO),
                new EmptyBorder(8, 8, 8, 8)));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private void initTablaSur() {
        String[] columns = { "ID", "Código Viaje", "Tripulante", "Estado", "Fecha Registro" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setSelectionForeground(COLOR_ACCENTO);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 180));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        add(sp, BorderLayout.SOUTH);
    }

    public void limpiarFilasTripulantes() {
        tripulantesContainer.removeAll();
        filasTripulantes.clear();
        tripulantesContainer.revalidate();
        tripulantesContainer.repaint();
    }

    public void agregarFilaTripulante(Trabajador t, String estadoEnBd) {
        TripulanteFila fila = new TripulanteFila(t, estadoEnBd);
        filasTripulantes.add(fila);
        tripulantesContainer.add(fila);
        tripulantesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public void finalizarFilas() {
        tripulantesContainer.revalidate();
        tripulantesContainer.repaint();
    }

    public List<TripulanteFila> getFilasTripulantes() {
        return Collections.unmodifiableList(filasTripulantes);
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

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JButton getBtnGuardarTodo() {
        return btnGuardarTodo;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JLabel getErrPlanificacion() {
        return errPlanificacion;
    }
}
