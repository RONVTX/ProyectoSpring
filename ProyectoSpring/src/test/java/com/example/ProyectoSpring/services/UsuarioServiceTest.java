package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsuarioService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Usuario")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private RegistroUsuarioDTO registroDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registroDTO = RegistroUsuarioDTO.builder()
                .email("test@example.com")
                .nombre("Juan")
                .apellido("Pérez")
                .password("password123")
                .passwordConfirmacion("password123")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .email("test@example.com")
                .nombre("Juan")
                .apellido("Pérez")
                .password("password123")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario exitosamente")
    void testRegistrarUsuarioExitoso() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.registrarUsuario(registroDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        assertThat(resultado.getApellido()).isEqualTo("Pérez");
        assertThat(resultado.getActivo()).isTrue();
        
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya está registrado")
    void testRegistrarUsuarioEmailDuplicado() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email ya está registrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si las contraseñas no coinciden")
    void testRegistrarUsuarioPasswordNoCoincide() {
        // Arrange
        registroDTO.setPasswordConfirmacion("otraPassword");

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrarUsuario(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Datos inválidos. Las contraseñas no coinciden");
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID")
    void testObtenerUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Debe obtener un usuario por email")
    void testObtenerUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerPorEmail("test@example.com");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe verificar si un email existe")
    void testExisteEmail() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean existe = usuarioService.existeEmail("test@example.com");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Debe desactivar un usuario")
    void testDesactivarUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.desactivarUsuario(1L);

        // Assert
        assertThat(resultado.getActivo()).isFalse();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al desactivar usuario que no existe")
    void testDesactivarUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.desactivarUsuario(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no encontrado");
    }
}
