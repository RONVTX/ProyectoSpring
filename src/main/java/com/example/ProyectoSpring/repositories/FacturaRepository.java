package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Factura
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByUsuario(Usuario usuario);
    
    List<Factura> findByEstado(EstadoFactura estado);
    
    List<Factura> findByUsuarioAndEstado(Usuario usuario, EstadoFactura estado);
    
    List<Factura> findByEstadoAndFechaVencimientoBefore(EstadoFactura estado, LocalDateTime fecha);
}
