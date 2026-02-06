package com.example.ProyectoSpring.enums;

import java.math.BigDecimal;

/**
 * Planes de suscripción disponibles en la plataforma
 */
public enum NivelPlan {
    BASIC(
        "Basic",
        new BigDecimal("9.99"),
        "Plan básico para pequeños negocios",
        100
    ),
    PREMIUM(
        "Premium",
        new BigDecimal("29.99"),
        "Plan premium con características avanzadas",
        500
    ),
    ENTERPRISE(
        "Enterprise",
        new BigDecimal("99.99"),
        "Plan empresarial con soporte prioritario",
        5000
    );

    private final String nombre;
    private final BigDecimal precioPorMes;
    private final String descripcion;
    private final int limiteCaracteristicas;

    NivelPlan(String nombre, BigDecimal precioPorMes, String descripcion, int limiteCaracteristicas) {
        this.nombre = nombre;
        this.precioPorMes = precioPorMes;
        this.descripcion = descripcion;
        this.limiteCaracteristicas = limiteCaracteristicas;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioPorMes() {
        return precioPorMes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getLimiteCaracteristicas() {
        return limiteCaracteristicas;
    }
}
