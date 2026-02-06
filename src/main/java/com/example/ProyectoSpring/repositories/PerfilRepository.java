package com.example.ProyectoSpring.repositories;

import com.example.ProyectoSpring.entities.Perfil;
import com.example.ProyectoSpring.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Perfil
 */
@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByUsuario(Usuario usuario);
}
