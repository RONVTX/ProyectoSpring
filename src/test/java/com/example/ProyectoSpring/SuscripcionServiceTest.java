package com.example.ProyectoSpring;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.PlanRepository;
import com.example.ProyectoSpring.repositories.SuscripcionRepository;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import com.example.ProyectoSpring.services.SuscripcionService;
import com.example.ProyectoSpring.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SuscripcionServiceTest {

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Plan planBasico;
    private Plan planPremium;

    @BeforeEach
    public void setUp() {
        // Crear usuario de prueba
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        usuario = usuarioService.registrarUsuario(dto);

        // Obtener planes existentes (inicializados por DataInitializer)
        planBasico = planRepository.findByNivel(NivelPlan.BASIC)
                .orElseThrow(() -> new RuntimeException("Plan BASIC no encontrado"));
        planPremium = planRepository.findByNivel(NivelPlan.PREMIUM)
                .orElseThrow(() -> new RuntimeException("Plan PREMIUM no encontrado"));

        // Desactivar el plan ENTERPRISE para el test de plan no encontrado
        Plan planEnterprise = planRepository.findByNivel(NivelPlan.ENTERPRISE)
                .orElseThrow(() -> new RuntimeException("Plan ENTERPRISE no encontrado"));
        planEnterprise.setActivo(false);
        planRepository.save(planEnterprise);
    }

    @Test
    public void testCrearSuscripcionExitosa() {
        // When
        Suscripcion suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);

        // Then
        assertThat(suscripcion).isNotNull();
        assertThat(suscripcion.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(suscripcion.getPlanActual().getNivel()).isEqualTo(NivelPlan.BASIC);
        assertThat(suscripcion.getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
    }

    @Test
    public void testCrearSuscripcionUsuarioNoExistente() {
        // When & Then
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(999L, NivelPlan.BASIC))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    public void testCrearSuscripcionPlanNoExistente() {
        // When & Then
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.ENTERPRISE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Plan no encontrado");
    }

    @Test
    public void testCrearSuscripcionUsuarioYaTieneActiva() {
        // Given
        suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);

        // When & Then
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.PREMIUM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El usuario ya tiene una suscripción activa");
    }

    @Test
    public void testObtenerSuscripcionActiva() {
        // Given
        suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);

        // When
        var suscripcion = suscripcionService.obtenerSuscripcionActiva(usuario.getId());

        // Then
        assertThat(suscripcion).isPresent();
        assertThat(suscripcion.get().getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
    }

    @Test
    public void testCambiarPlanExitoso() {
        // Given
        Suscripcion suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);

        // When
        Suscripcion suscripcionActualizada = suscripcionService.cambiarPlan(suscripcion.getId(), NivelPlan.PREMIUM);

        // Then
        assertThat(suscripcionActualizada.getPlanActual().getNivel()).isEqualTo(NivelPlan.PREMIUM);
    }

    @Test
    public void testCambiarPlanSuscripcionNoExistente() {
        // When & Then
        assertThatThrownBy(() -> suscripcionService.cambiarPlan(999L, NivelPlan.PREMIUM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Suscripción no encontrada");
    }

    @Test
    public void testCambiarPlanSuscripcionNoActiva() {
        // Given
        Suscripcion suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);
        suscripcionService.cancelarSuscripcion(suscripcion.getId());

        // When & Then
        assertThatThrownBy(() -> suscripcionService.cambiarPlan(suscripcion.getId(), NivelPlan.PREMIUM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Solo se puede cambiar el plan de una suscripción activa");
    }

    @Test
    public void testCancelarSuscripcion() {
        // Given
        Suscripcion suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);

        // When
        Suscripcion suscripcionCancelada = suscripcionService.cancelarSuscripcion(suscripcion.getId());

        // Then
        assertThat(suscripcionCancelada.getEstado()).isEqualTo(EstadoSuscripcion.CANCELADA);
        assertThat(suscripcionCancelada.getFechaCancelacion()).isNotNull();
    }

    @Test
    public void testCancelarSuscripcionNoExistente() {
        // When & Then
        assertThatThrownBy(() -> suscripcionService.cancelarSuscripcion(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Suscripción no encontrada");
    }
}