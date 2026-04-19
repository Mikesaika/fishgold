package com.mycompany.fishgold.models;

import java.util.Date;
import java.sql.Time;
import java.util.Objects;

/**
 * Representa una jornada de pesca o faena.
 * Incluye campos para mapeo relacional y visualización en UI.
 */
public class Faena {
    private int id;
    private String nombre;
    private Date fecha;
    private Time horaEmbarco;
    private int embarcacionId;
    private String ruta;
    private String estado;

    // Campo de utilidad para evitar JOINs innecesarios en la vista
    private String embarcacionNombre;

    public Faena() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHoraEmbarco() {
        return horaEmbarco;
    }

    public void setHoraEmbarco(Time horaEmbarco) {
        this.horaEmbarco = horaEmbarco;
    }

    public int getEmbarcacionId() {
        return embarcacionId;
    }

    public void setEmbarcacionId(int embarcacionId) {
        this.embarcacionId = embarcacionId;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmbarcacionNombre() {
        return embarcacionNombre;
    }

    public void setEmbarcacionNombre(String embarcacionNombre) {
        this.embarcacionNombre = embarcacionNombre;
    }

    // Compara faenas por ID.

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Faena faena = (Faena) o;
        return id == faena.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " [" + estado + "]";
    }
}