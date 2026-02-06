package com.example.ProyectoSpring.enums;

/**
 * Estados posibles de una suscripción
 */
public enum EstadoSuscripcion {
    ACTIVA("Activa"),
    CANCELADA("Cancelada"),
    MOROSA("Morosa"),
    PAUSA("Pausa");

    private final String descripcion;

    EstadoSuscripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
