package com.mycompany.fishgold.models;

import java.util.Objects;

/**
 * Modelo que representa la relación entre un trabajador y una faena.
 * Maneja estados de asistencia mediante tipos Wrapper (Boolean) para soportar
 * NULL.
 */
public class Tripulante {
    private int id;
    private int faenaId;
    private int trabajadorId;
    private int cargoId;
    private String descripcion;
    private Boolean asistenciaEmbarco;
    private Boolean asistenciaDesembarco;

    private String trabajadorNombre;
    private String nombreCargo;

    public Tripulante() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFaenaId() {
        return faenaId;
    }

    public void setFaenaId(int faenaId) {
        this.faenaId = faenaId;
    }

    public int getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(int trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getAsistenciaEmbarco() {
        return asistenciaEmbarco;
    }

    public void setAsistenciaEmbarco(Boolean asistenciaEmbarco) {
        this.asistenciaEmbarco = asistenciaEmbarco;
    }

    public Boolean getAsistenciaDesembarco() {
        return asistenciaDesembarco;
    }

    public void setAsistenciaDesembarco(Boolean asistenciaDesembarco) {
        this.asistenciaDesembarco = asistenciaDesembarco;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tripulante that = (Tripulante) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return trabajadorNombre + " - " + nombreCargo;
    }
}