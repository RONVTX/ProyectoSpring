package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Suscripción
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    Optional<Suscripcion> findByUsuarioAndEstado(Usuario usuario, EstadoSuscripcion estado);
    
    List<Suscripcion> findByEstado(EstadoSuscripcion estado);
    
    List<Suscripcion> findByUsuario(Usuario usuario);
    
    @Query("SELECT s FROM Suscripcion s WHERE s.estado = :estado AND s.fechaProximoPago <= :fecha")
    List<Suscripcion> findSuscripcionesParaRenovar(@Param("estado") EstadoSuscripcion estado, @Param("fecha") LocalDateTime fecha);
}
