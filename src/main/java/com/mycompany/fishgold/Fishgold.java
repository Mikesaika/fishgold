package com.mycompany.fishgold;

import com.mycompany.fishgold.controllers.LoginController;
import com.mycompany.fishgold.models.UsuarioDAO;
import com.mycompany.fishgold.views.LoginView;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Fishgold {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.err.println("No se pudo configurar el estilo visual nativo.");
        }
        java.awt.EventQueue.invokeLater(() -> {
            LoginView loginView = new LoginView();
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            LoginController loginController = new LoginController(loginView, usuarioDAO);

            loginController.start();
        });
    }
}