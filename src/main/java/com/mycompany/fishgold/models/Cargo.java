package com.mycompany.fishgold.models;

import java.util.Objects;

public class Cargo {
    private int id;
    private String nombreCargo;

    public Cargo() {
    }

    public Cargo(int id, String nombreCargo) {
        this.id = id;
        this.nombreCargo = nombreCargo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        Cargo cargo = (Cargo) o;
        return id == cargo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombreCargo;
    }
}