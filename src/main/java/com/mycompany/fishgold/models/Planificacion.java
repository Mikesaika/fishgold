package com.mycompany.fishgold.models;

import java.sql.Date;

public class Planificacion {
    private int id;
    private String codigoViaje;
    private String embarcacionNombre;
    private String destinoRuta;
    private Date fechaSalidaProgramada;
    private double montoBaseTripulacion;
    private double metaPesoKg;
    private String estado;

    public Planificacion() {
    }

    public Planificacion(int id, String codigoViaje, String embarcacionNombre, String destinoRuta, Date fechaSalidaProgramada, double montoBaseTripulacion, double metaPesoKg, String estado) {
        this.id = id;
        this.codigoViaje = codigoViaje;
        this.embarcacionNombre = embarcacionNombre;
        this.destinoRuta = destinoRuta;
        this.fechaSalidaProgramada = fechaSalidaProgramada;
        this.montoBaseTripulacion = montoBaseTripulacion;
        this.metaPesoKg = metaPesoKg;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigoViaje() { return codigoViaje; }
    public void setCodigoViaje(String codigoViaje) { this.codigoViaje = codigoViaje; }
    public String getEmbarcacionNombre() { return embarcacionNombre; }
    public void setEmbarcacionNombre(String embarcacionNombre) { this.embarcacionNombre = embarcacionNombre; }
    public String getDestinoRuta() { return destinoRuta; }
    public void setDestinoRuta(String destinoRuta) { this.destinoRuta = destinoRuta; }
    public Date getFechaSalidaProgramada() { return fechaSalidaProgramada; }
    public void setFechaSalidaProgramada(Date fechaSalidaProgramada) { this.fechaSalidaProgramada = fechaSalidaProgramada; }
    public double getMontoBaseTripulacion() { return montoBaseTripulacion; }
    public void setMontoBaseTripulacion(double montoBaseTripulacion) { this.montoBaseTripulacion = montoBaseTripulacion; }
    public double getMetaPesoKg() { return metaPesoKg; }
    public void setMetaPesoKg(double metaPesoKg) { this.metaPesoKg = metaPesoKg; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return codigoViaje + " (" + embarcacionNombre + ")";
    }
}
