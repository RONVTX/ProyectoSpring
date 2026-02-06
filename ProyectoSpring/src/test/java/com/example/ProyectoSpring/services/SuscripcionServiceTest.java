package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.*;
import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.SuscripcionRepository;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import com.example.ProyectoSpring.repositories.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para SuscripcionService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Suscripción")
class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private FacturaService facturaService;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private Usuario usuario;
    private Plan planBasic;
    private Plan planPremium;
    private Suscripcion suscripcion;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuario = Usuario.builder()
                .id(1L)
                .email("test@example.com")
                .nombre("Juan")
                .apellido("Pérez")
                .password("password123")
                .activo(true)
                .build();

        // Crear planes de prueba
        planBasic = Plan.builder()
                .id(1L)
                .nivel(NivelPlan.BASIC)
                .nombre("Basic")
                .descripcion("Plan básico")
                .precioMensual(new BigDecimal("9.99"))
                .limiteFeatures(100)
                .activo(true)
                .build();

        planPremium = Plan.builder()
                .id(2L)
                .nivel(NivelPlan.PREMIUM)
                .nombre("Premium")
                .descripcion("Plan premium")
                .precioMensual(new BigDecimal("29.99"))
                .limiteFeatures(500)
                .activo(true)
                .build();

        // Crear suscripción de prueba
        suscripcion = Suscripcion.builder()
                .id(1L)
                .usuario(usuario)
                .planActual(planBasic)
                .estado(EstadoSuscripcion.ACTIVA)
                .autoRenovar(true)
                .fechaInicio(LocalDateTime.now())
                .fechaProximoPago(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("Debe crear una nueva suscripción exitosamente")
    void testCrearSuscripcionExitoso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(planRepository.findByNivel(NivelPlan.BASIC)).thenReturn(Optional.of(planBasic));
        when(suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());
        when(suscripcionRepository.save(any(Suscripcion.class))).thenReturn(suscripcion);

        // Act
        Suscripcion resultado = suscripcionService.crearSuscripcion(1L, NivelPlan.BASIC);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getPlanActual()).isEqualTo(planBasic);
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
        assertThat(resultado.getAutoRenovar()).isTrue();
        
        verify(suscripcionRepository).save(any(Suscripcion.class));
        verify(facturaService).generarFacturaParaSuscripcion(any(Suscripcion.class), isNull());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void testCrearSuscripcionUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(999L, NivelPlan.BASIC))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario ya tiene suscripción activa")
    void testCrearSuscripcionUsuarioConSuscripcionActiva() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(planRepository.findByNivel(NivelPlan.BASIC)).thenReturn(Optional.of(planBasic));
        when(suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcion));

        // Act & Assert
        assertThatThrownBy(() -> suscripcionService.crearSuscripcion(1L, NivelPlan.BASIC))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El usuario ya tiene una suscripción activa");
    }

    @Test
    @DisplayName("Debe cambiar el plan y generar prorrateo correctamente")
    void testCambiarPlanConProrrateo() {
        // Arrange
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcion));
        when(planRepository.findByNivel(NivelPlan.PREMIUM)).thenReturn(Optional.of(planPremium));
        when(suscripcionRepository.save(any(Suscripcion.class))).thenReturn(suscripcion);

        // Act
        Suscripcion resultado = suscripcionService.cambiarPlan(1L, NivelPlan.PREMIUM);

        // Assert
        assertThat(resultado.getPlanActual()).isEqualTo(planPremium);
        verify(facturaService).generarFacturaParaSuscripcion(any(Suscripcion.class), eq(planBasic));
    }

    @Test
    @DisplayName("Debe cancelar una suscripción exitosamente")
    void testCancelarSuscripcion() {
        // Arrange
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcion));
        when(suscripcionRepository.save(any(Suscripcion.class))).thenReturn(suscripcion);

        // Act
        Suscripcion resultado = suscripcionService.cancelarSuscripcion(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoSuscripcion.CANCELADA);
        assertThat(resultado.getAutoRenovar()).isFalse();
        assertThat(resultado.getFechaCancelacion()).isNotNull();
    }

    @Test
    @DisplayName("Debe obtener la suscripción activa de un usuario")
    void testObtenerSuscripcionActiva() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcion));

        // Act
        Optional<Suscripcion> resultado = suscripcionService.obtenerSuscripcionActiva(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(suscripcion);
    }
}
