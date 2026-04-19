package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaenaDAO {

    public boolean create(Faena f) {
        String sql = "INSERT INTO faenas (nombre, fecha, hora_embarco, embarcacion_id, ruta, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNombre());
            ps.setDate(2, new java.sql.Date(f.getFecha().getTime()));
            ps.setTime(3, f.getHoraEmbarco());
            ps.setInt(4, f.getEmbarcacionId());
            ps.setString(5, f.getRuta());
            ps.setString(6, f.getEstado() == null ? "Pendiente" : f.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear faena: " + e.getMessage());
            return false;
        }
    }

    public List<Faena> readAll() {
        List<Faena> list = new ArrayList<>();
        String sql = "SELECT f.*, e.nombre as embarcacion_nombre " +
                "FROM faenas f " +
                "INNER JOIN embarcaciones e ON f.embarcacion_id = e.id " +
                "ORDER BY f.fecha DESC, f.hora_embarco DESC";
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

    public Faena readById(int id) {
        String sql = "SELECT f.*, e.nombre as embarcacion_nombre " +
                "FROM faenas f " +
                "INNER JOIN embarcaciones e ON f.embarcacion_id = e.id " +
                "WHERE f.id = ?";
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
        String sql = "SELECT COUNT(*) FROM faenas WHERE estado = ?";
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

    public boolean update(Faena f) {
        String sql = "UPDATE faenas SET nombre=?, fecha=?, hora_embarco=?, embarcacion_id=?, ruta=?, estado=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNombre());
            ps.setDate(2, new java.sql.Date(f.getFecha().getTime()));
            ps.setTime(3, f.getHoraEmbarco());
            ps.setInt(4, f.getEmbarcacionId());
            ps.setString(5, f.getRuta());
            ps.setString(6, f.getEstado());
            ps.setInt(7, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM faenas WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Faena mapResultSetToEntity(ResultSet rs) throws SQLException {
        Faena f = new Faena();
        f.setId(rs.getInt("id"));
        f.setNombre(rs.getString("nombre"));
        f.setFecha(rs.getDate("fecha"));
        f.setHoraEmbarco(rs.getTime("hora_embarco"));
        f.setEmbarcacionId(rs.getInt("embarcacion_id"));
        f.setRuta(rs.getString("ruta"));
        f.setEstado(rs.getString("estado"));
        f.setEmbarcacionNombre(rs.getString("embarcacion_nombre"));
        return f;
    }
}