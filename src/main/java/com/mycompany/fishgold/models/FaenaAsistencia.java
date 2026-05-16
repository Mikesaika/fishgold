package com.mycompany.fishgold.models;

import java.sql.Timestamp;

public class FaenaAsistencia {
    private int id;
    private int planificacionId;
    private int trabajadorId;
    private Timestamp fechaAsistencia;
    private String estadoAsistencia = "Presente";

    // Estos campos son vitales para el "Reporte Vivo" en la UI
    private String planificacionCodigo;
    private String trabajadorNombre;

    public FaenaAsistencia() {
    }

    // Constructor completo para el Mapper del DAO
    public FaenaAsistencia(int id, int planificacionId, int trabajadorId, Timestamp fechaAsistencia,
            String estadoAsistencia, String planificacionCodigo, String trabajadorNombre) {
        this.id = id;
        this.planificacionId = planificacionId;
        this.trabajadorId = trabajadorId;
        this.fechaAsistencia = fechaAsistencia;
        this.estadoAsistencia = estadoAsistencia != null ? estadoAsistencia : "Presente";
        this.planificacionCodigo = planificacionCodigo;
        this.trabajadorNombre = trabajadorNombre;
    }

    // Getters y Setters (Mantener los tuyos, están perfectos)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanificacionId() {
        return planificacionId;
    }

    public void setPlanificacionId(int planificacionId) {
        this.planificacionId = planificacionId;
    }

    public int getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(int trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public Timestamp getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(Timestamp fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public String getPlanificacionCodigo() {
        return planificacionCodigo;
    }

    public void setPlanificacionCodigo(String planificacionCodigo) {
        this.planificacionCodigo = planificacionCodigo;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public String getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }

    @Override
    public String toString() {
        return "Asistencia: " + trabajadorNombre + " en viaje " + planificacionCodigo;
    }
}