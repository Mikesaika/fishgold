package com.mycompany.fishgold.models;

import java.sql.Timestamp;

public class LiquidacionCaptura {
    private int id;
    private int planificacionId;
    private double pesoTotalPescado;
    private double montoFinalCalculado;
    private String registradoPorCapitan;
    private Timestamp fechaCierre;
    private String observaciones;

    // Campo vital para mostrar el Código del Viaje en la Tabla y Dashboard
    private String planificacionCodigo;

    public LiquidacionCaptura() {
    }

    /**
     * Constructor completo para evitar errores en el DAO (Mapper).
     * Incluye los 7 campos de la tabla + el código de planificación del JOIN.
     */
    public LiquidacionCaptura(int id, int planificacionId, double pesoTotalPescado,
            double montoFinalCalculado, String registradoPorCapitan,
            Timestamp fechaCierre, String observaciones, String planificacionCodigo) {
        this.id = id;
        this.planificacionId = planificacionId;
        this.pesoTotalPescado = pesoTotalPescado;
        this.montoFinalCalculado = montoFinalCalculado;
        this.registradoPorCapitan = registradoPorCapitan;
        this.fechaCierre = fechaCierre;
        this.observaciones = observaciones;
        this.planificacionCodigo = planificacionCodigo;
    }

    // Getters y Setters
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

    public double getPesoTotalPescado() {
        return pesoTotalPescado;
    }

    public void setPesoTotalPescado(double pesoTotalPescado) {
        this.pesoTotalPescado = pesoTotalPescado;
    }

    public double getMontoFinalCalculado() {
        return montoFinalCalculado;
    }

    public void setMontoFinalCalculado(double montoFinalCalculado) {
        this.montoFinalCalculado = montoFinalCalculado;
    }

    public String getRegistradoPorCapitan() {
        return registradoPorCapitan;
    }

    public void setRegistradoPorCapitan(String registradoPorCapitan) {
        this.registradoPorCapitan = registradoPorCapitan;
    }

    public Timestamp getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Timestamp fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPlanificacionCodigo() {
        return planificacionCodigo;
    }

    public void setPlanificacionCodigo(String planificacionCodigo) {
        this.planificacionCodigo = planificacionCodigo;
    }
}