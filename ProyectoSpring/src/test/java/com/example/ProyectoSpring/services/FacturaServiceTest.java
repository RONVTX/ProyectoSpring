package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoFactura;
import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para FacturaService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Factura")
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private ProrrateoService prorrateoService;

    @InjectMocks
    private FacturaService facturaService;

    private Usuario usuario;
    private Plan plan;
    private Suscripcion suscripcion;
    private Factura factura;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .email("test@example.com")
                .nombre("Juan")
                .apellido("Pérez")
                .activo(true)
                .build();

        plan = Plan.builder()
                .id(2L)
                .nivel(NivelPlan.PREMIUM)
                .nombre("Premium")
                .precioMensual(new BigDecimal("29.99"))
                .activo(true)
                .build();

        suscripcion = Suscripcion.builder()
                .id(1L)
                .usuario(usuario)
                .planActual(plan)
                .estado(EstadoSuscripcion.ACTIVA)
                .fechaProximoPago(LocalDateTime.now().plusDays(30))
                .build();

        factura = Factura.builder()
                .id(1L)
                .numeroFactura("FAC-001")
                .usuario(usuario)
                .suscripcion(suscripcion)
                .plan(plan)
                .montoBase(new BigDecimal("29.99"))
                .montoProrrateo(BigDecimal.ZERO)
                .montoTotal(new BigDecimal("29.99"))
                .estado(EstadoFactura.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Debe generar factura correctamente para una suscripción")
    void testGenerarFacturaParaSuscripcion() {
        // Arrange
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        Factura resultado = facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getSuscripcion()).isEqualTo(suscripcion);
        assertThat(resultado.getPlan()).isEqualTo(plan);
        assertThat(resultado.getMontoBase()).isEqualTo(new BigDecimal("29.99"));
        assertThat(resultado.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
        
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe calcular prorrateo al cambiar de plan en factura")
    void testGenerarFacturaConProrrateo() {
        // Arrange
        Plan planAnterior = Plan.builder()
                .id(2L)
                .nivel(NivelPlan.BASIC)
                .nombre("Basic")
                .precioMensual(new BigDecimal("9.99"))
                .activo(true)
                .build();

        // El plan actual (Premium) es más caro que el anterior
        factura.setPlan(suscripcion.getPlanActual());
        
        when(prorrateoService.calcularProrrateo(
                eq(suscripcion.getFechaProximoPago()),
                eq(planAnterior.getPrecioMensual()),
                eq(suscripcion.getPlanActual().getPrecioMensual())
        )).thenReturn(new BigDecimal("10.00"));
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        Factura resultado = facturaService.generarFacturaParaSuscripcion(suscripcion, planAnterior);

        // Assert
        assertThat(resultado).isNotNull();
        verify(prorrateoService).calcularProrrateo(
                eq(suscripcion.getFechaProximoPago()),
                eq(planAnterior.getPrecioMensual()),
                eq(suscripcion.getPlanActual().getPrecioMensual())
        );
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe obtener factura por número")
    void testObtenerPorNumero() {
        // Arrange
        when(facturaRepository.findByNumeroFactura("FAC-001")).thenReturn(Optional.of(factura));

        // Act
        Optional<Factura> resultado = facturaService.obtenerPorNumero("FAC-001");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroFactura()).isEqualTo("FAC-001");
    }

    @Test
    @DisplayName("Debe obtener facturas pendientes")
    void testObtenerFacturasPendientes() {
        // Arrange
        List<Factura> facturasPendientes = Arrays.asList(factura);
        when(facturaRepository.findByEstado(EstadoFactura.PENDIENTE))
                .thenReturn(facturasPendientes);

        // Act
        List<Factura> resultado = facturaService.obtenerFacturasPendientes();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    @DisplayName("Debe obtener facturas vencidas")
    void testObtenerFacturasVencidas() {
        // Arrange
        factura.setFechaVencimiento(LocalDateTime.now().minusDays(5));
        List<Factura> facturasVencidas = Arrays.asList(factura);
        when(facturaRepository.findByEstadoAndFechaVencimientoBefore(
                eq(EstadoFactura.PENDIENTE),
                any(LocalDateTime.class)
        )).thenReturn(facturasVencidas);

        // Act
        List<Factura> resultado = facturaService.obtenerFacturasVencidas();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe marcar factura como pagada")
    void testMarcarComoPagada() {
        // Arrange
        factura.setEstado(EstadoFactura.PENDIENTE);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // Act
        Factura resultado = facturaService.marcarComoPagada(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoFactura.PAGADA);
        assertThat(resultado.getFechaPago()).isNotNull();
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al marcar como pagada factura inexistente")
    void testMarcarComoPagadaFacturaNoExiste() {
        // Arrange
        when(facturaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> facturaService.marcarComoPagada(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Factura no encontrada");
    }

    @Test
    @DisplayName("Debe obtener factura por ID")
    void testObtenerPorId() {
        // Arrange
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        // Act
        Optional<Factura> resultado = facturaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }
}
