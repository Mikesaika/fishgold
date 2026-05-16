package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    /**
     * Activo y sin planificación "ocupada": asignado a un viaje activo que aún no tiene liquidación.
     */
    public List<Trabajador> readDisponibles() {
        return readDisponiblesParaPlanificacion(null);
    }

    /**
     * Trabajadores elegibles para tripulación. Si excludePlanId no es null, incluye también
     * quienes ya están asignados a ese viaje (para poder editar la misma planificación).
     */
    public List<Trabajador> readDisponiblesParaPlanificacion(Integer excludePlanId) {
        List<Trabajador> lista = new ArrayList<>();
        String ocupado = "SELECT fa.trabajador_id FROM faena_asistencia fa "
                + "INNER JOIN planificaciones p ON fa.planificacion_id = p.id "
                + "WHERE p.activo = 1 AND NOT EXISTS ("
                + "  SELECT 1 FROM liquidacion_captura l WHERE l.planificacion_id = p.id)";

        String sql;
        if (excludePlanId != null && excludePlanId > 0) {
            sql = "SELECT * FROM trabajadores WHERE estado = 'Activo' AND ( id NOT IN (" + ocupado + ") "
                    + "OR id IN (SELECT trabajador_id FROM faena_asistencia WHERE planificacion_id = ?) ) "
                    + "ORDER BY nombre_completo ASC";
        } else {
            sql = "SELECT * FROM trabajadores WHERE estado = 'Activo' AND id NOT IN (" + ocupado + ") "
                    + "ORDER BY nombre_completo ASC";
        }

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            if (excludePlanId != null && excludePlanId > 0) {
                ps.setInt(1, excludePlanId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer trabajadores disponibles: " + e.getMessage());
        }
        return lista;
    }

    /** True si el trabajador está en algún viaje sin liquidar (distinto de excludePlanId si aplica). */
    public boolean estaOcupadoSinLiquidar(int trabajadorId, Integer excludePlanId) {
        String sql = "SELECT COUNT(*) FROM faena_asistencia fa "
                + "INNER JOIN planificaciones p ON fa.planificacion_id = p.id "
                + "WHERE fa.trabajador_id = ? AND p.activo = 1 "
                + "AND NOT EXISTS (SELECT 1 FROM liquidacion_captura l WHERE l.planificacion_id = p.id) ";
        if (excludePlanId != null && excludePlanId > 0) {
            sql += "AND p.id <> ?";
        }
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trabajadorId);
            if (excludePlanId != null && excludePlanId > 0) {
                ps.setInt(2, excludePlanId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean create(Trabajador trabajador) {
        String sql = "INSERT INTO trabajadores (cedula_dni, nombre_completo, rol_cargo, telefono, direccion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            setPreparedStatement(ps, trabajador);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear trabajador: " + e.getMessage());
            return false;
        }
    }

    public List<Trabajador> readByPlanificacion(int planificacionId) {
        List<Trabajador> lista = new ArrayList<>();
        String sql = "SELECT t.* FROM trabajadores t "
                + "INNER JOIN faena_asistencia fa ON fa.trabajador_id = t.id "
                + "WHERE fa.planificacion_id = ? ORDER BY t.nombre_completo ASC";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, planificacionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer tripulación del viaje: " + e.getMessage());
        }
        return lista;
    }

    public List<Trabajador> readAll() {
        List<Trabajador> lista = new ArrayList<>();
        String sql = "SELECT * FROM trabajadores ORDER BY nombre_completo ASC";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al leer trabajadores: " + e.getMessage());
        }
        return lista;
    }

    public boolean update(Trabajador trabajador) {
        String sql = "UPDATE trabajadores SET cedula_dni=?, nombre_completo=?, rol_cargo=?, telefono=?, direccion=?, estado=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            setPreparedStatement(ps, trabajador);
            ps.setInt(7, trabajador.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar trabajador: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM trabajadores WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar trabajador: " + e.getMessage());
            return false;
        }
    }

    public List<Trabajador> search(String query) {
        List<Trabajador> lista = new ArrayList<>();
        String sql = "SELECT * FROM trabajadores WHERE cedula_dni LIKE ? OR nombre_completo LIKE ? OR rol_cargo LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            String param = "%" + query + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda de trabajadores: " + e.getMessage());
        }
        return lista;
    }

    public int countByEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM trabajadores WHERE estado = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar trabajadores: " + e.getMessage());
        }
        return 0;
    }

    private void setPreparedStatement(PreparedStatement ps, Trabajador t) throws SQLException {
        ps.setString(1, t.getCedulaDni());
        ps.setString(2, t.getNombreCompleto());
        ps.setString(3, t.getRolCargo());
        ps.setString(4, t.getTelefono());
        ps.setString(5, t.getDireccion());
        ps.setString(6, t.getEstado());
    }

    private Trabajador mapResultSet(ResultSet rs) throws SQLException {
        return new Trabajador(
                rs.getInt("id"),
                rs.getString("cedula_dni"),
                rs.getString("nombre_completo"),
                rs.getString("rol_cargo"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("estado"),
                rs.getTimestamp("fecha_registro"));
    }
}