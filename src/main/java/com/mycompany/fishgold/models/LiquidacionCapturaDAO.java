package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LiquidacionCapturaDAO {

    public boolean create(LiquidacionCaptura liqui) {
        // Consultas necesarias: Precio base de configuración y Meta de la planificación
        String sqlConfig = "SELECT pago_kilo_base FROM configuracion_pago WHERE id = 1";
        String sqlPlan = "SELECT meta_peso_kg FROM planificaciones WHERE id = ?";
        String sqlInsert = "INSERT INTO liquidacion_captura (planificacion_id, peso_total_pescado, monto_final_calculado, registrado_por_capitan, observaciones) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdatePlan = "UPDATE planificaciones SET estado = 'Finalizado' WHERE id = ?";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            double precioBase = 0;
            double metaPeso = 0;
            double pesoReal = liqui.getPesoTotalPescado();

            // 1. Obtener el precio por kilo configurado actualmente
            try (Statement stConf = con.createStatement();
                    ResultSet rsConf = stConf.executeQuery(sqlConfig)) {
                if (rsConf.next()) {
                    precioBase = rsConf.getDouble("pago_kilo_base");
                } else {
                    precioBase = 10.0; // Valor de respaldo por si la tabla está vacía
                }
            }

            // 2. Obtener meta de la planificación
            try (PreparedStatement psPlan = con.prepareStatement(sqlPlan)) {
                psPlan.setInt(1, liqui.getPlanificacionId());
                try (ResultSet rsPlan = psPlan.executeQuery()) {
                    if (rsPlan.next()) {
                        metaPeso = rsPlan.getDouble("meta_peso_kg");
                    } else {
                        return false;
                    }
                }
            }

            // 3. LÓGICA DE NEGOCIO ACTUALIZADA: Pago por kilo + Exceso al Doble
            double montoFinal = 0;
            if (pesoReal > metaPeso) {
                double exceso = pesoReal - metaPeso;
                // Kilos hasta la meta al precio base + Exceso al doble
                montoFinal = (metaPeso * precioBase) + (exceso * (precioBase * 2));
            } else {
                // Si no superó la meta, se paga todo al precio base
                montoFinal = pesoReal * precioBase;
            }

            // 4. Insertar Liquidación
            try (PreparedStatement psIns = con.prepareStatement(sqlInsert)) {
                psIns.setInt(1, liqui.getPlanificacionId());
                psIns.setDouble(2, pesoReal);
                psIns.setDouble(3, montoFinal);
                psIns.setString(4, liqui.getRegistradoPorCapitan());
                psIns.setString(5, liqui.getObservaciones());
                psIns.executeUpdate();
            }

            // 5. Cerrar Planificación
            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdatePlan)) {
                psUpd.setInt(1, liqui.getPlanificacionId());
                psUpd.executeUpdate();
            }

            con.commit(); // CONFIRMACIÓN TOTAL
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<LiquidacionCaptura> readAll() {
        List<LiquidacionCaptura> lista = new ArrayList<>();
        String sql = "SELECT l.*, p.codigo_viaje FROM liquidacion_captura l " +
                "INNER JOIN planificaciones p ON l.planificacion_id = p.id ORDER BY l.id DESC";
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

    public List<LiquidacionCaptura> search(String query) {
        List<LiquidacionCaptura> lista = new ArrayList<>();
        String sql = "SELECT l.*, p.codigo_viaje FROM liquidacion_captura l " +
                "INNER JOIN planificaciones p ON l.planificacion_id = p.id " +
                "WHERE p.codigo_viaje LIKE ? OR l.registrado_por_capitan LIKE ?";
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

    private LiquidacionCaptura mapResultSet(ResultSet rs) throws SQLException {
        return new LiquidacionCaptura(
                rs.getInt("id"),
                rs.getInt("planificacion_id"),
                rs.getDouble("peso_total_pescado"),
                rs.getDouble("monto_final_calculado"),
                rs.getString("registrado_por_capitan"),
                rs.getTimestamp("fecha_cierre"),
                rs.getString("observaciones"),
                rs.getString("codigo_viaje"));
    }
}