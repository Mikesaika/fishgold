package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public List<Cargo> readAll() {
        List<Cargo> list = new ArrayList<>();
        String sql = "SELECT * FROM cargos ORDER BY nombre_cargo ASC";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Cargo(rs.getInt("id"), rs.getString("nombre_cargo")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Cargo readByName(String nombre) {
        String sql = "SELECT * FROM cargos WHERE nombre_cargo = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cargo(rs.getInt("id"), rs.getString("nombre_cargo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}