package com.example.ProyectoSpring.enums;

/**
 * Estados de las facturas
 */
public enum EstadoFactura {
    PENDIENTE("Pendiente de pago"),
    PAGADA("Pagada"),
    VENCIDA("Vencida"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoFactura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
