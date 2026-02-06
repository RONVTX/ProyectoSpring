package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar planes
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;

    /**
     * Inicializa los planes disponibles en el sistema
     * Debe ser llamado una sola vez al inicio
     */
    @Transactional
    public void inicializarPlanes() {
        for (NivelPlan nivel : NivelPlan.values()) {
            if (!planRepository.existsByNivel(nivel)) {
                Plan plan = Plan.builder()
                        .nivel(nivel)
                        .nombre(nivel.getNombre())
                        .descripcion(nivel.getDescripcion())
                        .precioMensual(nivel.getPrecioPorMes())
                        .limiteFeatures(nivel.getLimiteCaracteristicas())
                        .activo(true)
                        .build();
                planRepository.save(plan);
            }
        }
    }

    /**
     * Obtiene un plan por su nivel
     */
    public Optional<Plan> obtenerPlanPorNivel(NivelPlan nivel) {
        return planRepository.findByNivel(nivel);
    }

    /**
     * Obtiene un plan por ID
     */
    public Optional<Plan> obtenerPlanPorId(Long id) {
        return planRepository.findById(id);
    }

    /**
     * Obtiene todos los planes activos
     */
    public List<Plan> obtenerPlanesActivos() {
        List<Plan> planes = planRepository.findAll();
        return planes.stream()
                .filter(Plan::getActivo)
                .toList();
    }
}
