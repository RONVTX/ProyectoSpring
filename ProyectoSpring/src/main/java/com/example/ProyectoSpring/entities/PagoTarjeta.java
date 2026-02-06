package com.example.ProyectoSpring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/**
 * Pago mediante Tarjeta de Crédito/Débito
 * Hereda de Pago
 */
@Entity
@Audited
@DiscriminatorValue("TARJETA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PagoTarjeta extends Pago {

    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;

    @Column(name = "titular")
    private String titular;

    @Column(name = "mes_vencimiento")
    private String mesVencimiento;

    @Column(name = "anio_vencimiento")
    private String anioVencimiento;
}
