package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmbarcacionDAO {

    public boolean create(Embarcacion e) {
        String sql = "INSERT INTO embarcaciones (nombre, propietario, modelo, capacidad, anio_compra, matricula, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            setParams(ps, e);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al crear embarcación: " + ex.getMessage());
            return false;
        }
    }

    public List<Embarcacion> readAll() {
        List<Embarcacion> list = new ArrayList<>();
        String sql = "SELECT * FROM embarcaciones ORDER BY nombre ASC";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public Embarcacion readById(int id) {
        String sql = "SELECT * FROM embarcaciones WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapResultSetToEntity(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int countByEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM embarcaciones WHERE estado = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public boolean update(Embarcacion e) {
        String sql = "UPDATE embarcaciones SET nombre=?, propietario=?, modelo=?, capacidad=?, anio_compra=?, matricula=?, estado=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            setParams(ps, e);
            ps.setInt(8, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM embarcaciones WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            // Esto fallará si hay faenas vinculadas (Integridad Referencial)
            return false;
        }
    }

    // MÉTODOS PRIVADOS PARA EVITAR REPETIR CÓDIGO (DRY)
    private void setParams(PreparedStatement ps, Embarcacion e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setString(2, e.getPropietario());
        ps.setString(3, e.getModelo());
        ps.setInt(4, e.getCapacidad());
        ps.setInt(5, e.getAnioCompra());
        ps.setString(6, e.getMatricula());
        ps.setString(7, e.getEstado());
    }

    private Embarcacion mapResultSetToEntity(ResultSet rs) throws SQLException {
        Embarcacion e = new Embarcacion();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setPropietario(rs.getString("propietario"));
        e.setModelo(rs.getString("modelo"));
        e.setCapacidad(rs.getInt("capacidad"));
        e.setAnioCompra(rs.getInt("anio_compra"));
        e.setMatricula(rs.getString("matricula"));
        e.setEstado(rs.getString("estado"));
        return e;
    }
}