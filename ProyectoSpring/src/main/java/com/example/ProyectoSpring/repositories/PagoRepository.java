package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Pago;
import com.example.ProyectoSpring.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByUsuario(Usuario usuario);
    
    Optional<Pago> findByReferenciaTransaccion(String referencia);
    
    List<Pago> findByProcesado(Boolean procesado);
}
