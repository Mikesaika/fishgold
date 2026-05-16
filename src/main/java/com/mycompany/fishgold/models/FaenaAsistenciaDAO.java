package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaenaAsistenciaDAO {

    private static final String SELECT_BASE = "SELECT fa.id, fa.planificacion_id, fa.trabajador_id, fa.fecha_asistencia, "
            + "fa.estado_asistencia, p.codigo_viaje, t.nombre_completo "
            + "FROM faena_asistencia fa "
            + "INNER JOIN planificaciones p ON fa.planificacion_id = p.id "
            + "INNER JOIN trabajadores t ON fa.trabajador_id = t.id ";

    public boolean create(FaenaAsistencia asistencia) {
        String sql = "INSERT INTO faena_asistencia (planificacion_id, trabajador_id, estado_asistencia) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, asistencia.getPlanificacionId());
            ps.setInt(2, asistencia.getTrabajadorId());
            ps.setString(3, asistencia.getEstadoAsistencia() != null ? asistencia.getEstadoAsistencia() : "Presente");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear asistencia: " + e.getMessage());
            return false;
        }
    }

    public FaenaAsistencia findByPlanificacionYTrabajador(int planificacionId, int trabajadorId) {
        String sql = SELECT_BASE + "WHERE fa.planificacion_id = ? AND fa.trabajador_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, planificacionId);
            ps.setInt(2, trabajadorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateEstado(int id, String estado) {
        String sql = "UPDATE faena_asistencia SET estado_asistencia = ?, fecha_asistencia = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar asistencia: " + e.getMessage());
            return false;
        }
    }

    public List<FaenaAsistencia> readByPlanificacion(int planificacionId) {
        List<FaenaAsistencia> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE fa.planificacion_id = ? ORDER BY t.nombre_completo ASC";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, planificacionId);
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

    public List<FaenaAsistencia> readAll() {
        List<FaenaAsistencia> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.activo = 1 ORDER BY fa.id DESC";
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

    public List<FaenaAsistencia> search(String query) {
        List<FaenaAsistencia> lista = new ArrayList<>();
        String sql = SELECT_BASE
                + "WHERE p.activo = 1 AND (p.codigo_viaje LIKE ? OR t.nombre_completo LIKE ?) ORDER BY fa.id DESC";
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

    private FaenaAsistencia mapResultSet(ResultSet rs) throws SQLException {
        String estado;
        try {
            estado = rs.getString("estado_asistencia");
        } catch (SQLException e) {
            estado = "Presente";
        }
        return new FaenaAsistencia(
                rs.getInt("id"),
                rs.getInt("planificacion_id"),
                rs.getInt("trabajador_id"),
                rs.getTimestamp("fecha_asistencia"),
                estado,
                rs.getString("codigo_viaje"),
                rs.getString("nombre_completo"));
    }
}
