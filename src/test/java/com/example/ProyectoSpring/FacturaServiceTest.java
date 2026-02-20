package com.example.ProyectoSpring;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.EstadoFactura;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.PlanRepository;
import com.example.ProyectoSpring.services.FacturaService;
import com.example.ProyectoSpring.services.SuscripcionService;
import com.example.ProyectoSpring.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FacturaServiceTest {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private PlanRepository planRepository;

    private Usuario usuario;
    private Plan plan;
    private Suscripcion suscripcion;

    @BeforeEach
    public void setUp() {
        // Crear usuario
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail("test@example.com");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        usuario = usuarioService.registrarUsuario(dto);

        // Obtener plan existente
        plan = planRepository.findByNivel(NivelPlan.BASIC)
                .orElseThrow(() -> new RuntimeException("Plan BASIC no encontrado"));

        // Crear suscripción
        suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), NivelPlan.BASIC);
    }

    @Test
    public void testGenerarFacturaParaSuscripcion() {
        // When
        Factura factura = facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // Then
        assertThat(factura).isNotNull();
        assertThat(factura.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(factura.getSuscripcion().getId()).isEqualTo(suscripcion.getId());
        assertThat(factura.getPlan().getId()).isEqualTo(plan.getId());
        assertThat(factura.getMontoBase()).isEqualTo(plan.getPrecioMensual());
        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
        assertThat(factura.getNumeroFactura()).isNotNull();
    }

    @Test
    public void testObtenerPorNumeroFactura() {
        // Given
        Factura factura = facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // When
        var facturaEncontrada = facturaService.obtenerPorNumero(factura.getNumeroFactura());

        // Then
        assertThat(facturaEncontrada).isPresent();
        assertThat(facturaEncontrada.get().getId()).isEqualTo(factura.getId());
    }

    @Test
    public void testObtenerFacturasUsuario() {
        // Given
        facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // When
        List<Factura> facturas = facturaService.obtenerFacturasUsuario(usuario.getId());

        // Then
        assertThat(facturas).isNotEmpty();
        assertThat(facturas.get(0).getUsuario().getId()).isEqualTo(usuario.getId());
    }

    @Test
    public void testObtenerFacturasPendientes() {
        // Given
        facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // When
        List<Factura> facturasPendientes = facturaService.obtenerFacturasPendientes();

        // Then
        assertThat(facturasPendientes).isNotEmpty();
        assertThat(facturasPendientes.get(0).getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
    }

    @Test
    public void testMarcarComoPagada() {
        // Given
        Factura factura = facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        // When
        Factura facturaPagada = facturaService.marcarComoPagada(factura.getId());

        // Then
        assertThat(facturaPagada.getEstado()).isEqualTo(EstadoFactura.PAGADA);
        assertThat(facturaPagada.getFechaPago()).isNotNull();
    }

    @Test
    public void testMarcarComoPagadaFacturaNoExistente() {
        // When & Then
        assertThatThrownBy(() -> facturaService.marcarComoPagada(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Factura no encontrada");
    }

    @Test
    public void testBuscarFacturasPorRangoFechas() {
        // Given
        Factura factura = facturaService.generarFacturaParaSuscripcion(suscripcion, null);
        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now().plusDays(1);

        // When
        List<Factura> facturas = facturaService.buscarFacturas(desde, hasta, null, null);

        // Then
        assertThat(facturas).isNotEmpty();
    }

    @Test
    public void testBuscarFacturasPorRangoMontos() {
        // Given
        facturaService.generarFacturaParaSuscripcion(suscripcion, null);
        BigDecimal minMonto = new BigDecimal("5.00");
        BigDecimal maxMonto = new BigDecimal("20.00");

        // When
        List<Factura> facturas = facturaService.buscarFacturas(null, null, minMonto, maxMonto);

        // Then
        assertThat(facturas).isNotEmpty();
        assertThat(facturas.get(0).getMontoTotal()).isBetween(minMonto, maxMonto);
    }
}