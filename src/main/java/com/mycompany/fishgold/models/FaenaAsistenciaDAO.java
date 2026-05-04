package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaenaAsistenciaDAO {

    // Registrar nueva asistencia
    public boolean create(FaenaAsistencia asistencia) {
        String sql = "INSERT INTO faena_asistencia (planificacion_id, trabajador_id) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, asistencia.getPlanificacionId());
            ps.setInt(2, asistencia.getTrabajadorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear asistencia: " + e.getMessage());
            return false;
        }
    }

    // Listado total con JOINs para la tabla de la UI
    public List<FaenaAsistencia> readAll() {
        List<FaenaAsistencia> lista = new ArrayList<>();
        String sql = "SELECT fa.id, fa.planificacion_id, fa.trabajador_id, fa.fecha_asistencia, " +
                "p.codigo_viaje, t.nombre_completo " +
                "FROM faena_asistencia fa " +
                "INNER JOIN planificaciones p ON fa.planificacion_id = p.id " +
                "INNER JOIN trabajadores t ON fa.trabajador_id = t.id " +
                "ORDER BY fa.id DESC";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Búsqueda dinámica por código de viaje o nombre de trabajador
    public List<FaenaAsistencia> search(String query) {
        List<FaenaAsistencia> lista = new ArrayList<>();
        String sql = "SELECT fa.id, fa.planificacion_id, fa.trabajador_id, fa.fecha_asistencia, " +
                "p.codigo_viaje, t.nombre_completo " +
                "FROM faena_asistencia fa " +
                "INNER JOIN planificaciones p ON fa.planificacion_id = p.id " +
                "INNER JOIN trabajadores t ON fa.trabajador_id = t.id " +
                "WHERE p.codigo_viaje LIKE ? OR t.nombre_completo LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            String param = "%" + query + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * CORRECCIÓN: Mapeo usando el constructor de 6 parámetros definido en el
     * Modelo.
     */
    private FaenaAsistencia mapResultSet(ResultSet rs) throws SQLException {
        return new FaenaAsistencia(
                rs.getInt("id"),
                rs.getInt("planificacion_id"),
                rs.getInt("trabajador_id"),
                rs.getTimestamp("fecha_asistencia"),
                rs.getString("codigo_viaje"), // Parámetro 5: String
                rs.getString("nombre_completo") // Parámetro 6: String
        );
    }
}