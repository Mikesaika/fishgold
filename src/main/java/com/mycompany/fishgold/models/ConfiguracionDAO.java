package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;

/**
 * DAO para la gestión del precio maestro del kilo.
 * Basado en la regla de negocio de fila única (ID = 1).
 */
public class ConfiguracionDAO {

    /**
     * Obtiene la configuración actual desde la BD.
     * Si la tabla está vacía, intenta crear el registro inicial o devuelve el
     * default.
     */
    public Configuracion getActual() {
        String sql = "SELECT id, pago_kilo_base FROM configuracion_pago WHERE id = 1";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Configuracion(rs.getInt("id"), rs.getDouble("pago_kilo_base"));
            } else {
                // Si por alguna razón no existe el ID 1, lo insertamos con un valor base de
                // $5.00
                insertDefault(2.50);
            }
        } catch (SQLException e) {
            System.err.println("Error crítico en ConfiguracionDAO.getActual: " + e.getMessage());
        }

        // Retorno de emergencia para que la App no explote
        return new Configuracion(1, 2.50);
    }

    /**
     * Actualiza el precio usando la lógica de "Insertar o Actualizar" (Upsert).
     * Esto garantiza que siempre se trabaje sobre el ID 1.
     */
    public boolean update(double nuevoPrecio) {
        String sql = "INSERT INTO configuracion_pago (id, pago_kilo_base) VALUES (1, ?) " +
                "ON DUPLICATE KEY UPDATE pago_kilo_base = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, nuevoPrecio);
            ps.setDouble(2, nuevoPrecio);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar precio maestro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método privado para asegurar que el registro ID=1 exista siempre.
     */
    private void insertDefault(double precio) {
        String sql = "INSERT IGNORE INTO configuracion_pago (id, pago_kilo_base) VALUES (1, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, precio);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Silencioso, ya que es un proceso preventivo
        }
    }
}