package com.mycompany.fishgold.models;

import com.mycompany.fishgold.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanificacionDAO {

    /**
     * Crea la planificación y vincula a la tripulación en una sola transacción.
     */
    public boolean createConTripulacion(Planificacion plan, List<Trabajador> tripulacion) {
        String sqlPlan = "INSERT INTO planificaciones (codigo_viaje, embarcacion_nombre, destino_ruta, "
                + "fecha_salida_programada, meta_peso_kg, estado) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlAsistencia = "INSERT INTO faena_asistencia (planificacion_id, trabajador_id) VALUES (?, ?)";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlPlan, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, plan.getCodigoViaje());
                ps.setString(2, plan.getEmbarcacionNombre());
                ps.setString(3, plan.getDestinoRuta());
                ps.setDate(4, plan.getFechaSalidaProgramada());
                ps.setDouble(5, plan.getMetaPesoKg());
                ps.setString(6, plan.getEstado());
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int planId = generatedKeys.getInt(1);
                        try (PreparedStatement psAsis = con.prepareStatement(sqlAsistencia)) {
                            for (Trabajador t : tripulacion) {
                                psAsis.setInt(1, planId);
                                psAsis.setInt(2, t.getId());
                                psAsis.addBatch();
                            }
                            psAsis.executeBatch();
                        }
                    }
                }
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * MÉTODO NUEVO: Actualiza los datos del viaje y refresca la tripulación
     * asignada.
     * Este es el método que le falta a tu controlador.
     */
    public boolean updateConTripulacion(Planificacion plan, List<Trabajador> tripulacion) {
        String sqlUpdate = "UPDATE planificaciones SET codigo_viaje=?, embarcacion_nombre=?, destino_ruta=?, "
                + "fecha_salida_programada=?, meta_peso_kg=?, estado=? WHERE id=?";
        String sqlDelAsis = "DELETE FROM faena_asistencia WHERE planificacion_id=?";
        String sqlInsAsis = "INSERT INTO faena_asistencia (planificacion_id, trabajador_id) VALUES (?, ?)";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Actualizar datos de la tabla planificaciones
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, plan.getCodigoViaje());
                ps.setString(2, plan.getEmbarcacionNombre());
                ps.setString(3, plan.getDestinoRuta());
                ps.setDate(4, plan.getFechaSalidaProgramada());
                ps.setDouble(5, plan.getMetaPesoKg());
                ps.setString(6, plan.getEstado());
                ps.setInt(7, plan.getId());
                ps.executeUpdate();
            }

            // 2. Borrar tripulación anterior para este viaje
            try (PreparedStatement psDel = con.prepareStatement(sqlDelAsis)) {
                psDel.setInt(1, plan.getId());
                psDel.executeUpdate();
            }

            // 3. Insertar la nueva tripulación seleccionada
            if (tripulacion != null && !tripulacion.isEmpty()) {
                try (PreparedStatement psIns = con.prepareStatement(sqlInsAsis)) {
                    for (Trabajador t : tripulacion) {
                        psIns.setInt(1, plan.getId());
                        psIns.setInt(2, t.getId());
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    public List<Planificacion> readAll() {
        List<Planificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM planificaciones ORDER BY id DESC";
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

    public boolean update(Planificacion plan) {
        String sql = "UPDATE planificaciones SET codigo_viaje=?, embarcacion_nombre=?, destino_ruta=?, "
                + "fecha_salida_programada=?, meta_peso_kg=?, estado=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, plan.getCodigoViaje());
            ps.setString(2, plan.getEmbarcacionNombre());
            ps.setString(3, plan.getDestinoRuta());
            ps.setDate(4, plan.getFechaSalidaProgramada());
            ps.setDouble(5, plan.getMetaPesoKg());
            ps.setString(6, plan.getEstado());
            ps.setInt(7, plan.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Borrado SEGURO: Limpia la asistencia antes de borrar la planificación.
     */
    public boolean delete(int id) {
        String sqlAsis = "DELETE FROM faena_asistencia WHERE planificacion_id=?";
        String sqlPlan = "DELETE FROM planificaciones WHERE id=?";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psAsis = con.prepareStatement(sqlAsis)) {
                psAsis.setInt(1, id);
                psAsis.executeUpdate();
            }

            try (PreparedStatement psPlan = con.prepareStatement(sqlPlan)) {
                psPlan.setInt(1, id);
                int rows = psPlan.executeUpdate();
                con.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    public List<Planificacion> search(String query) {
        List<Planificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM planificaciones WHERE codigo_viaje LIKE ? OR embarcacion_nombre LIKE ?";
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

    /**
     * Cuenta cuántas planificaciones existen con un estado específico.
     * Útil para las estadísticas del Dashboard.
     */
    public int countByEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM planificaciones WHERE estado = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar estados: " + e.getMessage());
        }
        return 0;
    }

    private Planificacion mapResultSet(ResultSet rs) throws SQLException {
        Planificacion p = new Planificacion();
        p.setId(rs.getInt("id"));
        p.setCodigoViaje(rs.getString("codigo_viaje"));
        p.setEmbarcacionNombre(rs.getString("embarcacion_nombre"));
        p.setDestinoRuta(rs.getString("destino_ruta"));
        p.setFechaSalidaProgramada(rs.getDate("fecha_salida_programada"));
        p.setMetaPesoKg(rs.getDouble("meta_peso_kg"));
        p.setEstado(rs.getString("estado"));
        return p;
    }
}