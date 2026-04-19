package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    public boolean create(Trabajador t) {
        String sql = "INSERT INTO trabajadores (nombre_completo, tiene_licencia, direccion, " +
                "contacto_emergencia_nombre, contacto_emergencia_relacion, " +
                "contacto_emergencia_telefono, puestos_anteriores, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            setParameters(ps, t);
            ps.setString(8, t.getEstado() == null ? "Activo" : t.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear trabajador: " + e.getMessage());
            return false;
        }
    }

    public List<Trabajador> readAll() {
        List<Trabajador> list = new ArrayList<>();
        String sql = "SELECT * FROM trabajadores ORDER BY nombre_completo ASC";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Trabajador readById(int id) {
        String sql = "SELECT * FROM trabajadores WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
        }
        return 0;
    }

    public boolean update(Trabajador t) {
        String sql = "UPDATE trabajadores SET nombre_completo=?, tiene_licencia=?, direccion=?, " +
                "contacto_emergencia_nombre=?, contacto_emergencia_relacion=?, " +
                "contacto_emergencia_telefono=?, puestos_anteriores=?, estado=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            setParameters(ps, t);
            ps.setString(8, t.getEstado());
            ps.setInt(9, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

            return false;
        }
    }

    private void setParameters(PreparedStatement ps, Trabajador t) throws SQLException {
        ps.setString(1, t.getNombreCompleto());
        ps.setBoolean(2, t.isTieneLicencia());
        ps.setString(3, t.getDireccion());
        ps.setString(4, t.getContactoEmergenciaNombre());
        ps.setString(5, t.getContactoEmergenciaRelacion());
        ps.setString(6, t.getContactoEmergenciaTelefono());
        ps.setString(7, t.getPuestosAnteriores());
    }

    private Trabajador mapResultSetToEntity(ResultSet rs) throws SQLException {
        Trabajador t = new Trabajador();
        t.setId(rs.getInt("id"));
        t.setNombreCompleto(rs.getString("nombre_completo"));
        t.setTieneLicencia(rs.getBoolean("tiene_licencia"));
        t.setDireccion(rs.getString("direccion"));
        t.setContactoEmergenciaNombre(rs.getString("contacto_emergencia_nombre"));
        t.setContactoEmergenciaRelacion(rs.getString("contacto_emergencia_relacion"));
        t.setContactoEmergenciaTelefono(rs.getString("contacto_emergencia_telefono"));
        t.setPuestosAnteriores(rs.getString("puestos_anteriores"));
        t.setEstado(rs.getString("estado"));
        return t;
    }
}