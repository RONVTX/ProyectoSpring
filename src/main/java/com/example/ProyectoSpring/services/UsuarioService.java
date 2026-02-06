package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio para gestionar usuarios
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema
     */
    public Usuario registrarUsuario(RegistroUsuarioDTO dto) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar DTO
        if (!dto.esValido()) {
            throw new IllegalArgumentException("Datos inválidos. Las contraseñas no coinciden");
        }

        Usuario usuario = Usuario.builder()
                .email(dto.getEmail())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene un usuario por email
     */
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verifica si un email existe
     */
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Desactiva un usuario
     */
    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }
}
