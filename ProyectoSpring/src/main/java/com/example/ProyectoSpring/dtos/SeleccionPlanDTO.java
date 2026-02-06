package com.example.ProyectoSpring.dtos;

import com.example.ProyectoSpring.enums.NivelPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para seleccionar un plan de suscripción
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeleccionPlanDTO {
    private Long usuarioId;
    private NivelPlan nivelPlan;
    private String metodoPago;
}
