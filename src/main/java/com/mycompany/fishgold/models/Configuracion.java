package com.mycompany.fishgold.models;

public class Configuracion {
    private int id; // Siempre será 1
    private double pagoKiloBase;

    public Configuracion() {
    }

    public Configuracion(int id, double pagoKiloBase) {
        this.id = id;
        this.pagoKiloBase = pagoKiloBase;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPagoKiloBase() {
        return pagoKiloBase;
    }

    public void setPagoKiloBase(double pagoKiloBase) {
        this.pagoKiloBase = pagoKiloBase;
    }
}