package com.example.ProyectoSpring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad base para diferentes tipos de pago
 * Usa herencia SINGLE_TABLE para soportar PayPal, Tarjeta, Transferencia
 */
@Entity
@Table(name = "pagos")
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_pago", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "referencia_transaccion")
    private String referenciaTransaccion;

    @Column(name = "procesado", nullable = false)
    private Boolean procesado;

    @PrePersist
    public void prePersist() {
        this.fechaPago = LocalDateTime.now();
        this.procesado = false;
    }
}
