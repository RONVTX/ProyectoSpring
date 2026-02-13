package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByUsuario(Usuario usuario);
    
    List<Factura> findByEstado(EstadoFactura estado);
    
    List<Factura> findByUsuarioAndEstado(Usuario usuario, EstadoFactura estado);
    
    List<Factura> findByEstadoAndFechaVencimientoBefore(EstadoFactura estado, LocalDateTime fecha);

    List<Factura> findByFechaEmisionBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Factura> findByMontoTotalBetween(java.math.BigDecimal desde, java.math.BigDecimal hasta);

    List<Factura> findByMontoTotalGreaterThanEqual(java.math.BigDecimal desde);

    List<Factura> findByMontoTotalLessThanEqual(java.math.BigDecimal hasta);
    
    List<Factura> findBySuscripcionAndEstado(Suscripcion suscripcion, EstadoFactura estado);
}
