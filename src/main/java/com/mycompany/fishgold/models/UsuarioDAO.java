package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * Valida el acceso al sistema.
     * 
     * @param username Nombre de usuario ingresado en el login.
     * @param password Contraseña ingresada.
     * @return Objeto Usuario si las credenciales coinciden, null en caso contrario.
     */
    public Usuario login(String username, String password) {
        String query = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            // Log de error más descriptivo para depuración
            System.err.println("Error de conexión a la base de datos (FishGold): " + e.getMessage());
        }
        return null;
    }

    private Usuario mapResultSetToEntity(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        // Mapeamos la contraseña por si necesitas validaciones extras en el Controller
        user.setPassword(rs.getString("password"));
        return user;
    }
}