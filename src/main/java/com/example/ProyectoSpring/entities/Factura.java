package com.example.ProyectoSpring.entities;

import com.example.ProyectoSpring.enums.EstadoFactura;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Factura generada automáticamente por suscripción
 */
@Entity
@Table(name = "facturas")
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_factura", unique = true, nullable = false)
    private String numeroFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "monto_base", nullable = false)
    private BigDecimal montoBase;

    @Column(name = "monto_prorrateo")
    private BigDecimal montoProrrateo;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
        this.estado = EstadoFactura.PENDIENTE;
        // Vencimiento a 15 días
        this.fechaVencimiento = LocalDateTime.now().plusDays(15);
        if (numeroFactura == null) {
            this.numeroFactura = "FAC-" + System.currentTimeMillis();
        }
    }
}
