package com.mycompany.fishgold.models;

import java.sql.Timestamp;

public class Trabajador {
    private int id;
    private String cedulaDni;
    private String nombreCompleto;
    private String rolCargo;
    private String telefono;
    private String direccion;
    private String estado;
    private Timestamp fechaRegistro;

    public Trabajador() {
    }

    public Trabajador(int id, String cedulaDni, String nombreCompleto, String rolCargo, String telefono,
            String direccion, String estado, Timestamp fechaRegistro) {
        this.id = id;
        this.cedulaDni = cedulaDni;
        this.nombreCompleto = nombreCompleto;
        this.rolCargo = rolCargo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters impecables
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedulaDni() {
        return cedulaDni;
    }

    public void setCedulaDni(String cedulaDni) {
        this.cedulaDni = cedulaDni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRolCargo() {
        return rolCargo;
    }

    public void setRolCargo(String rolCargo) {
        this.rolCargo = rolCargo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        // Formato ideal para ComboBoxes y Selección de Faena
        return nombreCompleto + " (" + rolCargo + ")";
    }
}