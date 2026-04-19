package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripulacionDAO {

    public boolean create(Tripulante t) {

        String sql = "INSERT INTO tripulacion (faena_id, trabajador_id, cargo_id, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getFaenaId());
            ps.setInt(2, t.getTrabajadorId());
            ps.setInt(3, t.getCargoId());
            ps.setString(4, t.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar tripulante: " + e.getMessage());
            return false;
        }
    }

    public List<Tripulante> readByFaena(int faenaId) {
        List<Tripulante> list = new ArrayList<>();
        String sql = "SELECT tp.*, tr.nombre_completo as trabajador_nombre, cg.nombre_cargo " +
                "FROM tripulacion tp " +
                "INNER JOIN trabajadores tr ON tp.trabajador_id = tr.id " +
                "INNER JOIN cargos cg ON tp.cargo_id = cg.id " +
                "WHERE tp.faena_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, faenaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAsistenciaEmbarco(int tripulanteId, Boolean asistencia) {
        String sql = "UPDATE tripulacion SET asistencia_embarco=?, fecha_asistencia_embarco=NOW() WHERE id=?";
        return ejecutarUpdateAsistencia(sql, asistencia, tripulanteId);
    }

    public boolean updateAsistenciaDesembarco(int tripulanteId, Boolean asistencia) {
        String sql = "UPDATE tripulacion SET asistencia_desembarco=?, fecha_asistencia_desembarco=NOW() WHERE id=?";
        return ejecutarUpdateAsistencia(sql, asistencia, tripulanteId);
    }

    private boolean ejecutarUpdateAsistencia(String sql, Boolean asistencia, int id) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            if (asistencia == null)
                ps.setNull(1, Types.BOOLEAN);
            else
                ps.setBoolean(1, asistencia);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tripulacion WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Tripulante mapResultSetToEntity(ResultSet rs) throws SQLException {
        Tripulante t = new Tripulante();
        t.setId(rs.getInt("id"));
        t.setFaenaId(rs.getInt("faena_id"));
        t.setTrabajadorId(rs.getInt("trabajador_id"));
        t.setCargoId(rs.getInt("cargo_id"));
        t.setNombreCargo(rs.getString("nombre_cargo"));
        t.setTrabajadorNombre(rs.getString("trabajador_nombre"));
        t.setDescripcion(rs.getString("descripcion"));
        t.setAsistenciaEmbarco(getObjectOrNull(rs, "asistencia_embarco"));
        t.setAsistenciaDesembarco(getObjectOrNull(rs, "asistencia_desembarco"));

        return t;
    }

    private Boolean getObjectOrNull(ResultSet rs, String colName) throws SQLException {
        boolean val = rs.getBoolean(colName);
        return rs.wasNull() ? null : val;
    }
}