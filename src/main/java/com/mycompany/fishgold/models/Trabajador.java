package com.mycompany.fishgold.models;

import java.util.Objects;

// Modelo que representa al personal de Fishgold.

public class Trabajador {
    private int id;
    private String nombreCompleto;
    private boolean tieneLicencia;
    private String direccion;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaRelacion;
    private String contactoEmergenciaTelefono;
    private String puestosAnteriores;
    private String estado;

    public Trabajador() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isTieneLicencia() {
        return tieneLicencia;
    }

    public void setTieneLicencia(boolean tieneLicencia) {
        this.tieneLicencia = tieneLicencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContactoEmergenciaNombre() {
        return contactoEmergenciaNombre;
    }

    public void setContactoEmergenciaNombre(String contactoEmergenciaNombre) {
        this.contactoEmergenciaNombre = contactoEmergenciaNombre;
    }

    public String getContactoEmergenciaRelacion() {
        return contactoEmergenciaRelacion;
    }

    public void setContactoEmergenciaRelacion(String contactoEmergenciaRelacion) {
        this.contactoEmergenciaRelacion = contactoEmergenciaRelacion;
    }

    public String getContactoEmergenciaTelefono() {
        return contactoEmergenciaTelefono;
    }

    public void setContactoEmergenciaTelefono(String contactoEmergenciaTelefono) {
        this.contactoEmergenciaTelefono = contactoEmergenciaTelefono;
    }

    public String getPuestosAnteriores() {
        return puestosAnteriores;
    }

    public void setPuestosAnteriores(String puestosAnteriores) {
        this.puestosAnteriores = puestosAnteriores;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Compara trabajadores por ID.
     * Evita que el sistema confunda dos objetos con el mismo nombre pero distinto
     * ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trabajador that = (Trabajador) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombreCompleto + (tieneLicencia ? " (Con Licencia)" : "");
    }
}