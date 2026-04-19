package com.mycompany.fishgold.controllers;

import com.mycompany.fishgold.models.Usuario;
import com.mycompany.fishgold.models.UsuarioDAO;
import com.mycompany.fishgold.views.LoginView;
import com.mycompany.fishgold.views.MainView;
import com.mycompany.fishgold.util.Validator;
import javax.swing.JOptionPane;

public class LoginController {

    private final LoginView view;
    private final UsuarioDAO dao;

    public LoginController(LoginView view, UsuarioDAO dao) {
        this.view = view;
        this.dao = dao;
        init();
    }

    private void init() {

        this.view.getBtnLogin().addActionListener(e -> login());
        // Permite dar 'Enter' en el campo de contraseña para loguear
        this.view.getTxtPassword().addActionListener(e -> login());

        this.view.setLocationRelativeTo(null); // Centrar ventana
    }

    public void start() {
        view.setVisible(true);
    }

    private void login() {
        String username = view.getUsername().trim();
        String password = view.getPassword().trim();
        if (!Validator.isNotBlank(username) || !Validator.isNotBlank(password)) {
            view.displayErrorMessage("Debe completar todos los campos.");
            return;
        }

        Usuario user = dao.login(username, password);

        if (user != null) {
            handleSuccessfulLogin(user);
        } else {
            view.displayErrorMessage("Usuario o contraseña incorrectos.");
        }
    }

    private void handleSuccessfulLogin(Usuario user) {
        JOptionPane.showMessageDialog(view,
                "Bienvenido a Fishgold\nUsuario: " + user.getUsername().toUpperCase(),
                "Acceso Concedido",
                JOptionPane.INFORMATION_MESSAGE);

        view.dispose();

        MainView mainView = new MainView();
        MainController mainController = new MainController(mainView);
        mainController.start();
    }
}