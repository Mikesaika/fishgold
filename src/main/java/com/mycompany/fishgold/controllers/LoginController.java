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
        // Listeners vinculados a la acción de login
        this.view.getBtnLogin().addActionListener(e -> login());

        // UX: Permite dar 'Enter' en los campos para no tener que usar el mouse
        this.view.getTxtUsername().addActionListener(e -> login());
        this.view.getTxtPassword().addActionListener(e -> login());

        this.view.setLocationRelativeTo(null); // Centrar ventana en pantalla
    }

    public void start() {
        view.setVisible(true);
    }

    private void login() {
        // 1. VALIDACIÓN VISUAL (Usando tu Validator mejorado)
        // Esto marcará en rojo los campos vacíos automáticamente
        boolean userOk = Validator.isNotBlank(view.getTxtUsername());
        boolean passOk = Validator.isNotBlank(view.getTxtPassword());

        if (!userOk || !passOk) {
            view.displayErrorMessage("Por favor, complete las credenciales de acceso.");
            return;
        }

        // 2. SEGURIDAD: Bloqueamos el botón para evitar múltiples clics mientras
        // consulta SQL
        view.getBtnLogin().setEnabled(false);

        String username = view.getUsername().trim();
        String password = view.getPassword().trim();

        // 3. CONSULTA AL DAO
        Usuario user = dao.login(username, password);

        if (user != null) {
            handleSuccessfulLogin(user);
        } else {
            view.getBtnLogin().setEnabled(true);
            view.displayErrorMessage("Credenciales inválidas. Intente de nuevo.");
            view.getTxtPassword().setText(""); // Limpiar contraseña por seguridad
            view.getTxtPassword().requestFocus();
        }
    }

    private void handleSuccessfulLogin(Usuario user) {
        // Feedback sólido estilo institucional
        JOptionPane.showMessageDialog(view,
                "Acceso autorizado para: " + user.getUsername().toUpperCase(),
                "Sistema FishGold",
                JOptionPane.INFORMATION_MESSAGE);

        view.dispose(); // Cerramos el login

        // Iniciamos el sistema principal
        MainView mainView = new MainView();

        // Pasamos el usuario logueado al MainController si necesitas
        // mostrar su nombre en el Dashboard o controlar permisos
        MainController mainController = new MainController(mainView);
        mainController.start();
    }
}