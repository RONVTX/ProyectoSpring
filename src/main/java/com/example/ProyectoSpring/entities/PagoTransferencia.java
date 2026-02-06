package com.example.ProyectoSpring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/**
 * Pago mediante Transferencia Bancaria
 * Hereda de Pago
 */
@Entity
@Audited
@DiscriminatorValue("TRANSFERENCIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PagoTransferencia extends Pago {

    @Column(name = "banco_origen")
    private String bancoOrigen;

    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @Column(name = "tipo_cuenta")
    private String tipoCuenta;
}
