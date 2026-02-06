package com.example.ProyectoSpring.entities;

import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

/**
 * Entidad Suscripción con auditoría
 * Registra todos los cambios de plan y estados
 */
@Entity
@Table(name = "suscripciones")
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan planActual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSuscripcion estado;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_renovacion")
    private LocalDateTime fechaRenovacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "fecha_proximo_pago")
    private LocalDateTime fechaProximoPago;

    @Column(name = "auto_renovar")
    private Boolean autoRenovar;

    @PrePersist
    public void prePersist() {
        this.fechaInicio = LocalDateTime.now();
        this.estado = EstadoSuscripcion.ACTIVA;
        this.autoRenovar = true;
        // Fecha próximo pago es 30 días a partir de ahora
        this.fechaProximoPago = LocalDateTime.now().plusDays(30);
        // Fecha renovación es igual a próximo pago
        this.fechaRenovacion = this.fechaProximoPago;
    }
}
