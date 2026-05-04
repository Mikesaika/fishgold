package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ConfiguracionPanel: Vista para gestionar los parámetros de pago del sistema.
 */
public class ConfiguracionPanel extends JPanel {
    private JTextField txtPagoBase;
    private JButton btnGuardar;
    private JLabel lblFeedback;

    // Paleta de Colores Institucional
    private final Color COLOR_FONDO = new Color(248, 250, 252);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_EXITO = new Color(22, 163, 74);
    private final Color COLOR_BORDE = new Color(226, 232, 240);

    public ConfiguracionPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(60, 60, 60, 60));

        initContent();
    }

    private void initContent() {
        // Contenedor principal alineado arriba
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setOpaque(false);

        // --- ENCABEZADO ---
        JLabel lblTitle = new JLabel("Configuración de Pagos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Establezca los valores financieros para las liquidaciones de faena.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- TARJETA DE AJUSTES ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(40, 40, 40, 40)));
        card.setMaximumSize(new Dimension(700, 350));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etiqueta y Campo de Pago Base
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblPago = new JLabel("PRECIO BASE POR KILO ($):");
        lblPago.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblPago, gbc);

        gbc.gridx = 1;
        txtPagoBase = new JTextField(10);
        txtPagoBase.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPagoBase.setPreferredSize(new Dimension(150, 40));
        card.add(txtPagoBase, gbc);

        // Info de Regla de Negocio (Explicación del excedente doble)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JLabel lblInfo = new JLabel("<html><body style='width: 450px;'>"
                + "<p style='color: #475569; font-size: 11px;'><b>INFORMACIÓN DE CÁLCULO:</b></p>"
                + "<p style='color: #64748b; font-size: 13px; margin-top: 5px;'>"
                + "El sistema utiliza el <b>Precio Base</b> para los kilos registrados hasta la meta de la planificación. "
                + "Si la captura supera dicha meta, cada kilo excedente se pagará automáticamente al <b>doble (2x)</b>."
                + "</p></body></html>");
        card.add(lblInfo, gbc);

        // Botón de Guardar
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.insets = new Insets(30, 10, 10, 10);
        gbc.anchor = GridBagConstraints.EAST;
        btnGuardar = new JButton("Guardar Configuración");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(COLOR_PRIMARIO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(200, 45));
        card.add(btnGuardar, gbc);

        // Label para Feedback (Éxito/Error)
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFeedback.setForeground(COLOR_EXITO);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(lblFeedback, gbc);

        // Ensamblaje
        mainContainer.add(lblTitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        mainContainer.add(lblSub);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 40)));
        mainContainer.add(card);

        add(mainContainer, BorderLayout.NORTH);
    }

    // Getters y Setters
    public JTextField getTxtPagoBase() {
        return txtPagoBase;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public void setFeedback(String message) {
        lblFeedback.setText(message);
    }
}