package com.example.ProyectoSpring.entities;

import com.example.ProyectoSpring.enums.NivelPlan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

/**
 * Entidad Plan con auditoría
 */
@Entity
@Table(name = "planes")
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NivelPlan nivel;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_mensual", nullable = false)
    private BigDecimal precioMensual;

    @Column(name = "limite_features")
    private Integer limiteFeatures;

    @Column(name = "activo")
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        this.activo = true;
    }
}
