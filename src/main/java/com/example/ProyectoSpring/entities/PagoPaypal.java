package com.example.ProyectoSpring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/**
 * Pago mediante PayPal
 * Hereda de Pago
 */
@Entity
@Audited
@DiscriminatorValue("PAYPAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PagoPaypal extends Pago {

    @Column(name = "email_paypal")
    private String emailPaypal;

    @Column(name = "id_transaccion_paypal")
    private String idTransaccionPaypal;
}
