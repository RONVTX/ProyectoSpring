package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.enums.NivelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Plan
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByNivel(NivelPlan nivel);
    boolean existsByNivel(NivelPlan nivel);
}
