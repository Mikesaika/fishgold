package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel principal con tarjetas, tendencia de asistencias (7 días) y resumen de kilos.
 */
public class DashboardPanel extends JPanel {

    private JLabel lblTotalTrabajadores;
    private JLabel lblViajesActivos;
    private JLabel lblIngresosLiquidos;
    private JLabel lblKilosSemana;
    private JLabel lblTotalLiquidaciones;
    private JButton btnActualizar;
    private JPanel chartPanel;

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT_MAIN = new Color(44, 62, 80);
    private static final Color TEXT_SEC = new Color(127, 140, 141);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color AMBER = new Color(243, 156, 18);
    private static final Color GREEN = new Color(46, 204, 113);

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 28));
        setBackground(BG);
        setBorder(new EmptyBorder(36, 40, 40, 40));

        initHeader();
        initCardsRow();
        initBottomSection();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Resumen operativo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("FishGold — visión rápida de personal, viajes y capturas.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(TEXT_SEC);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        left.add(sub);

        btnActualizar = new JButton("Actualizar datos");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(ACCENT);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setOpaque(true);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        header.add(left, BorderLayout.WEST);
        header.add(btnActualizar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void initCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 24, 0));
        row.setOpaque(false);

        lblTotalTrabajadores = makeCard(row, "Trabajadores activos", "[T]", ACCENT);
        lblViajesActivos = makeCard(row, "Viajes abiertos", "[V]", AMBER);
        lblIngresosLiquidos = makeCard(row, "Ingresos liquidados", "$", GREEN);

        add(row, BorderLayout.CENTER);
    }

    private void initBottomSection() {
        JPanel bottom = new JPanel(new GridLayout(1, 2, 24, 0));
        bottom.setOpaque(false);

        JPanel kilosCard = shadowCard();
        kilosCard.setLayout(new BorderLayout(0, 8));
        kilosCard.setBorder(new EmptyBorder(22, 24, 22, 24));
        JLabel t1 = new JLabel("Kilos faenados (7 días)");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t1.setForeground(TEXT_SEC);
        lblKilosSemana = new JLabel("0 kg");
        lblKilosSemana.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblKilosSemana.setForeground(TEXT_MAIN);
        kilosCard.add(t1, BorderLayout.NORTH);
        kilosCard.add(lblKilosSemana, BorderLayout.CENTER);

        JPanel liqCard = shadowCard();
        liqCard.setLayout(new BorderLayout(0, 8));
        liqCard.setBorder(new EmptyBorder(22, 24, 22, 24));
        JLabel t2 = new JLabel("Liquidaciones registradas");
        t2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t2.setForeground(TEXT_SEC);
        lblTotalLiquidaciones = new JLabel("0");
        lblTotalLiquidaciones.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotalLiquidaciones.setForeground(TEXT_MAIN);
        liqCard.add(t2, BorderLayout.NORTH);
        liqCard.add(lblTotalLiquidaciones, BorderLayout.CENTER);

        bottom.add(kilosCard);
        bottom.add(liqCard);

        chartPanel = new AsistenciaChartPanel(new int[7]);
        chartPanel.setOpaque(false);
        chartPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setOpaque(false);
        south.add(bottom);
        south.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel chartTitle = new JLabel("Asistencias registradas por día (últimos 7 días)");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartTitle.setForeground(TEXT_MAIN);
        chartTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        south.add(chartTitle);
        south.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel chartHolder = shadowCard();
        chartHolder.setLayout(new BorderLayout());
        chartHolder.setBorder(new EmptyBorder(16, 20, 20, 20));
        chartHolder.add(chartPanel, BorderLayout.CENTER);
        chartHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        south.add(chartHolder);

        add(south, BorderLayout.SOUTH);
    }

    private JLabel makeCard(JPanel parent, String title, String glyph, Color stripe) {
        JPanel card = shadowCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(22, 26, 26, 26));

        JLabel g = new JLabel(glyph);
        g.setFont(new Font("Consolas", Font.BOLD, 28));
        g.setForeground(stripe);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_SEC);

        JLabel value = new JLabel("—");
        value.setFont(new Font("Segoe UI", Font.BOLD, 40));
        value.setForeground(TEXT_MAIN);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);
        textCol.add(lblTitle);
        textCol.add(value);

        JPanel stripePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(stripe);
                g2.fillRoundRect(0, 8, 5, 52, 4, 4);
                g2.dispose();
            }
        };
        stripePanel.setOpaque(false);
        stripePanel.setPreferredSize(new Dimension(12, 1));

        JPanel inner = new JPanel(new BorderLayout(12, 0));
        inner.setOpaque(false);
        inner.add(stripePanel, BorderLayout.WEST);
        JPanel mid = new JPanel(new BorderLayout(0, 8));
        mid.setOpaque(false);
        mid.add(g, BorderLayout.NORTH);
        mid.add(textCol, BorderLayout.SOUTH);
        inner.add(mid, BorderLayout.CENTER);

        card.add(inner, BorderLayout.CENTER);
        parent.add(card);
        return value;
    }

    private JPanel shadowCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, 18, 18);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);
                g2.dispose();
            }
        };
    }

    public void setDatosAsistenciaChart(int[] valores7Dias) {
        if (chartPanel instanceof AsistenciaChartPanel) {
            ((AsistenciaChartPanel) chartPanel).setValues(valores7Dias);
            chartPanel.repaint();
        }
    }

    public JLabel getLblTotalTrabajadores() {
        return lblTotalTrabajadores;
    }

    public JLabel getLblViajesPendientes() {
        return lblViajesActivos;
    }

    public JLabel getLblTotalCapturas() {
        return lblIngresosLiquidos;
    }

    public JLabel getLblKilosSemana() {
        return lblKilosSemana;
    }

    public JLabel getLblTotalLiquidaciones() {
        return lblTotalLiquidaciones;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    private static class AsistenciaChartPanel extends JPanel {
        private int[] values = new int[7];
        private static final Color BAR = new Color(52, 152, 219);
        private static final Color LABEL_COL = new Color(127, 140, 141);

        AsistenciaChartPanel(int[] v) {
            setValues(v);
            setPreferredSize(new Dimension(400, 200));
        }

        void setValues(int[] v) {
            if (v != null && v.length == 7) {
                values = v.clone();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 36;
            int chartH = h - pad;
            int max = 1;
            for (int v : values) {
                max = Math.max(max, v);
            }

            g2.setColor(new Color(236, 240, 241));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            int n = values.length;
            int barW = (w - 2 * pad) / n - 8;
            for (int i = 0; i < n; i++) {
                int bh = (int) ((double) values[i] / max * (chartH - 30));
                int x = pad + i * (barW + 8);
                int y = chartH - bh;
                g2.setColor(BAR);
                g2.fill(new RoundRectangle2D.Double(x, y + 10, barW, bh, 6, 6));
                g2.setColor(LABEL_COL);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String[] labs = { "-6d", "-5d", "-4d", "-3d", "-2d", "-1d", "Hoy" };
                g2.drawString(labs[i], x + Math.max(0, barW / 2 - 12), chartH + 26);
            }
            g2.dispose();
        }
    }
}
