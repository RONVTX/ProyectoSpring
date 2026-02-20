package com.example.ProyectoSpring;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testRegistrarUsuarioExitoso() {
        // Given
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setPais("ES");

        // When
        Usuario usuario = usuarioService.registrarUsuario(dto);

        // Then
        assertThat(usuario).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
        assertThat(usuario.getNombre()).isEqualTo("Test");
        assertThat(usuario.getApellido()).isEqualTo("User");
        assertThat(usuario.getPais()).isEqualTo("ES");
        assertThat(usuario.getActivo()).isTrue(); // Según el código, se setea por defecto en @PrePersist
    }

    @Test
    public void testRegistrarUsuarioEmailDuplicado() {
        // Given
        RegistroUsuarioDTO dto1 = new RegistroUsuarioDTO();
        dto1.setEmail("duplicate@example.com");
        dto1.setNombre("Test1");
        dto1.setApellido("User1");
        dto1.setPassword("password123");
        dto1.setConfirmPassword("password123");

        RegistroUsuarioDTO dto2 = new RegistroUsuarioDTO();
        dto2.setEmail("duplicate@example.com");
        dto2.setNombre("Test2");
        dto2.setApellido("User2");
        dto2.setPassword("password123");
        dto2.setConfirmPassword("password123");

        // When
        usuarioService.registrarUsuario(dto1);

        // Then
        assertThatThrownBy(() -> usuarioService.registrarUsuario(dto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email ya está registrado");
    }

    @Test
    public void testRegistrarUsuarioContrasenasNoCoinciden() {
        // Given
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("differentpassword");

        // When & Then
        assertThatThrownBy(() -> usuarioService.registrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Datos inválidos. Las contraseñas no coinciden");
    }

    @Test
    public void testObtenerPorIdExistente() {
        // Given
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        Usuario usuarioGuardado = usuarioService.registrarUsuario(dto);

        // When
        var usuario = usuarioService.obtenerPorId(usuarioGuardado.getId());

        // Then
        assertThat(usuario).isPresent();
        assertThat(usuario.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testObtenerPorIdNoExistente() {
        // When
        var usuario = usuarioService.obtenerPorId(999L);

        // Then
        assertThat(usuario).isEmpty();
    }

    @Test
    public void testExisteEmail() {
        // Given
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        usuarioService.registrarUsuario(dto);

        // When & Then
        assertThat(usuarioService.existeEmail("test@example.com")).isTrue();
        assertThat(usuarioService.existeEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    public void testDesactivarUsuario() {
        // Given
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        Usuario usuario = usuarioService.registrarUsuario(dto);

        // When
        Usuario usuarioDesactivado = usuarioService.desactivarUsuario(usuario.getId());

        // Then
        assertThat(usuarioDesactivado.getActivo()).isFalse();
    }

    @Test
    public void testDesactivarUsuarioNoExistente() {
        // When & Then
        assertThatThrownBy(() -> usuarioService.desactivarUsuario(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no encontrado");
    }
}