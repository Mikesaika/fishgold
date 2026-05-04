package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    /**
     * MÉTODO DE EXPERTO: Lee solo trabajadores activos que NO están en faenas
     * vigentes.
     * Cruza la tabla de trabajadores con asistencia y planificaciones para
     * verificar disponibilidad.
     */
    public List<Trabajador> readDisponibles() {
        List<Trabajador> lista = new ArrayList<>();
        // SQL: Selecciona trabajadores Activos que NO tengan registros en
        // faena_asistencia
        // vinculados a planificaciones con estado 'Pendiente' o 'En Curso'.
        String sql = "SELECT * FROM trabajadores WHERE estado = 'Activo' AND id NOT IN (" +
                "  SELECT trabajador_id FROM faena_asistencia fa " +
                "  JOIN planificaciones p ON fa.planificacion_id = p.id " +
                "  WHERE p.estado IN ('Pendiente', 'En Curso')" +
                ") ORDER BY nombre_completo ASC";

        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al leer trabajadores disponibles: " + e.getMessage());
        }
        return lista;
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