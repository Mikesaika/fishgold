package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Consultas agregadas para el panel principal (tendencias y resúmenes).
 */
public class DashboardDAO {

    /**
     * Por día, conteo de filas en faena_asistencia con fecha_asistencia en ese día
     * (últimos 7 días, índice 0 = hace 6 días, 6 = hoy).
     */
    public int[] conteoAsistenciasUltimos7Dias() {
        int[] counts = new int[7];
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(6);
        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hastaExcl = hoy.plusDays(1).atStartOfDay();
        String sql = "SELECT DATE(fecha_asistencia) AS d, COUNT(*) AS c FROM faena_asistencia "
                + "WHERE fecha_asistencia >= ? AND fecha_asistencia < ? "
                + "GROUP BY DATE(fecha_asistencia)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hastaExcl));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date day = rs.getDate("d");
                    if (day == null)
                        continue;
                    LocalDate ld = day.toLocalDate();
                    int idx = (int) java.time.temporal.ChronoUnit.DAYS.between(inicio, ld);
                    if (idx >= 0 && idx < 7) {
                        counts[idx] = rs.getInt("c");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DashboardDAO conteoAsistencias: " + e.getMessage());
        }
        return counts;
    }

    /** Suma de kilos registrados en liquidaciones de los últimos 7 días (fecha_cierre). */
    public double kilosTotalesSemana() {
        String sql = "SELECT COALESCE(SUM(peso_total_pescado), 0) FROM liquidacion_captura "
                + "WHERE fecha_cierre >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("DashboardDAO kilosTotalesSemana: " + e.getMessage());
        }
        return 0;
    }

    public int countLiquidacionesTotales() {
        String sql = "SELECT COUNT(*) FROM liquidacion_captura";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
