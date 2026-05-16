package com.mycompany.fishgold.util;

/**
 * Regla de negocio: kilos hasta la meta al precio base; exceso al doble.
 */
public final class LiquidacionCalculo {

    private LiquidacionCalculo() {
    }

    public static double calcularMonto(double pesoReal, double metaKg, double precioPorKilo) {
        if (pesoReal > metaKg) {
            double exceso = pesoReal - metaKg;
            return (metaKg * precioPorKilo) + (exceso * (precioPorKilo * 2));
        }
        return pesoReal * precioPorKilo;
    }
}
