package com.mycompany.fishgold.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Labels para errores (Feedback en tiempo real)
    private JLabel errUser, errPass;

    // Paleta de colores SENAE/Moderno
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_TEXTO = new Color(15, 23, 42);
    private final Color COLOR_ERROR = new Color(220, 38, 38);

    public LoginView() {
        setTitle("Fishgold - Acceso al Sistema");
        setSize(450, 600); // Un poco más alto para los mensajes de error
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // --- ICONO ---
        JLabel lblIcon = new JLabel("FishGold");
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setForeground(COLOR_PRIMARIO);

        // --- TITULOS ---
        JLabel lblWelcome = new JLabel("¡Bienvenido!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblWelcome.setForeground(COLOR_TEXTO);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Ingresa tus credenciales");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(71, 85, 105));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- PANEL DE FORMULARIO (GRIDBAG PARA ESTABILIDAD) ---
        JPanel fieldPanel = new JPanel(new GridBagLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(350, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Usuario
        JLabel lblUser = new JLabel("Nombre de usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(71, 85, 105));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        fieldPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        styleField(txtUsername);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 2, 0);
        fieldPanel.add(txtUsername, gbc);

        errUser = createErrorLabel();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        fieldPanel.add(errUser, gbc);

        // Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(71, 85, 105));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        fieldPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 2, 0);
        fieldPanel.add(txtPassword, gbc);

        errPass = createErrorLabel();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        fieldPanel.add(errPass, gbc);

        // --- BOTÓN INICIAR SESIÓN ---
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(COLOR_PRIMARIO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setMaximumSize(new Dimension(350, 50));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- ENSAMBLAJE ---
        mainPanel.add(lblIcon);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(lblWelcome);
        mainPanel.add(lblSub);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(fieldPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnLogin);

        add(mainPanel);
    }

    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 42));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
    }

    private JLabel createErrorLabel() {
        JLabel l = new JLabel(" "); // Espacio en blanco para reservar sitio
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(COLOR_ERROR);
        return l;
    }

    // --- GETTERS Y MÉTODOS DE FEEDBACK ---
    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JLabel getErrUser() {
        return errUser;
    }

    public JLabel getErrPass() {
        return errPass;
    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error de Acceso", JOptionPane.ERROR_MESSAGE);
    }
}