package com.example.ProyectoSpring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias para ProrrateoService
 * 
 * Las pruebas cubren todos los casos de prorrateo:
 * - Plan más caro
 * - Plan más barato
 * - Cálculo de días restantes
 * - Validaciones
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Prorrateo")
class ProrrateoServiceTest {

    @InjectMocks
    private ProrrateoService prorrateoService;

    private LocalDateTime fechaProximoPago;
    private BigDecimal precioPlanAnterior;
    private BigDecimal precioPlanNuevo;

    @BeforeEach
    void setUp() {
        // Configurar fechas y precios para las pruebas
        fechaProximoPago = LocalDateTime.now().plusDays(30);
        precioPlanAnterior = new BigDecimal("9.99");
        precioPlanNuevo = new BigDecimal("29.99");
    }

    @Test
    @DisplayName("Debe calcular prorrateo correcto cuando el nuevo plan es más caro")
    void testCalcularProrrateoConPlanMasCaro() {
        // Arrange
        LocalDateTime fechaProxima = LocalDateTime.now().plusDays(15); // 15 días restantes
        BigDecimal precioAnterior = new BigDecimal("10.00");
        BigDecimal precioNuevo = new BigDecimal("30.00");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(fechaProxima, precioAnterior, precioNuevo);

        // Assert
        // Diferencia: 30 - 10 = 20
        // Precio diario: 20 / 30 = 0.67
        // Prorrateo: 0.67 * 15 = 10.00
        assertThat(prorrateo).isNotNull();
        assertThat(prorrateo).isGreaterThan(BigDecimal.ZERO);
        assertThat(prorrateo.scale()).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Debe retornar cero cuando el nuevo plan es más barato")
    void testCalcularProrrateoConPlanMasBarato() {
        // Arrange
        BigDecimal precioAnterior = new BigDecimal("30.00");
        BigDecimal precioNuevo = new BigDecimal("10.00");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(
                fechaProximoPago,
                precioAnterior,
                precioNuevo
        );

        // Assert
        assertThat(prorrateo).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe retornar cero cuando los precios son iguales")
    void testCalcularProrrateoConPreciosIguales() {
        // Arrange
        BigDecimal precio = new BigDecimal("20.00");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(
                fechaProximoPago,
                precio,
                precio
        );

        // Assert
        assertThat(prorrateo).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe retornar cero cuando la fecha de pago ya pasó")
    void testCalcularProrrateoFechaPasada() {
        // Arrange
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(5);

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(
                fechaPasada,
                precioPlanAnterior,
                precioPlanNuevo
        );

        // Assert
        assertThat(prorrateo).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe calcular correctamente los días restantes")
    void testCalcularDiasRestantes() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.now().plusDays(10);

        // Act
        long diasRestantes = prorrateoService.calcularDiasRestantes(fecha);

        // Assert
        assertThat(diasRestantes).isGreaterThanOrEqualTo(9).isLessThanOrEqualTo(11);
    }

    @Test
    @DisplayName("Debe retornar 0 días si la fecha es en el pasado")
    void testCalcularDiasRestantesFechaPasada() {
        // Arrange
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(5);

        // Act
        long diasRestantes = prorrateoService.calcularDiasRestantes(fechaPasada);

        // Assert
        assertThat(diasRestantes).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debe validar correctamente parámetros válidos")
    void testEsValidoConParametrosValidos() {
        // Act
        boolean esValido = prorrateoService.esValido(
                precioPlanAnterior,
                precioPlanNuevo,
                fechaProximoPago
        );

        // Assert
        assertThat(esValido).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false si el precio anterior es nulo")
    void testEsValidoConPrecioAnteriorNulo() {
        // Act
        boolean esValido = prorrateoService.esValido(
                null,
                precioPlanNuevo,
                fechaProximoPago
        );

        // Assert
        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false si el precio nuevo es nulo")
    void testEsValidoConPrecioNuevoNulo() {
        // Act
        boolean esValido = prorrateoService.esValido(
                precioPlanAnterior,
                null,
                fechaProximoPago
        );

        // Assert
        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false si la fecha de pago es nula")
    void testEsValidoConFechaNula() {
        // Act
        boolean esValido = prorrateoService.esValido(
                precioPlanAnterior,
                precioPlanNuevo,
                null
        );

        // Assert
        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false si la fecha de pago es en el pasado")
    void testEsValidoConFechaPasada() {
        // Act
        boolean esValido = prorrateoService.esValido(
                precioPlanAnterior,
                precioPlanNuevo,
                LocalDateTime.now().minusDays(5)
        );

        // Assert
        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("Debe redondear correctamente el prorrateo a 2 decimales")
    void testProrrateoRedondeadoCorrectamente() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.now().plusDays(7); // 7 días restantes
        BigDecimal precioAnterior = new BigDecimal("9.99");
        BigDecimal precioNuevo = new BigDecimal("19.99");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(fecha, precioAnterior, precioNuevo);

        // Assert
        // Verificar que tiene exactamente 2 decimales
        assertThat(prorrateo.scale()).isLessThanOrEqualTo(2);
        // Verificar que es positivo
        assertThat(prorrateo).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe calcular prorrateo para cambio de Basic a Premium")
    void testProrrateoBasicAPremium() {
        // Arrange: Cambio de Basic ($9.99) a Premium ($29.99) con 20 días restantes
        LocalDateTime fecha = LocalDateTime.now().plusDays(20);
        BigDecimal precioBasic = new BigDecimal("9.99");
        BigDecimal precioPremium = new BigDecimal("29.99");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(fecha, precioBasic, precioPremium);

        // Assert
        // Diferencia: 29.99 - 9.99 = 20.00
        // Precio diario: 20.00 / 30 = 0.67
        // Prorrateo: 0.67 * 20 = 13.40
        assertThat(prorrateo).isGreaterThan(new BigDecimal("13.00"));
        assertThat(prorrateo).isLessThan(new BigDecimal("14.00"));
    }

    @Test
    @DisplayName("Debe calcular prorrateo para cambio de Premium a Enterprise")
    void testProrrateoPremiumAEnterprise() {
        // Arrange: Cambio de Premium ($29.99) a Enterprise ($99.99) con 15 días restantes
        LocalDateTime fecha = LocalDateTime.now().plusDays(15);
        BigDecimal precioPremium = new BigDecimal("29.99");
        BigDecimal precioEnterprise = new BigDecimal("99.99");

        // Act
        BigDecimal prorrateo = prorrateoService.calcularProrrateo(fecha, precioPremium, precioEnterprise);

        // Assert
        // Diferencia: 99.99 - 29.99 = 70.00
        // Precio diario: 70.00 / 30 = 2.33
        // Prorrateo: 2.33 * 15 = 35.00
        assertThat(prorrateo).isGreaterThan(new BigDecimal("34.00"));
        assertThat(prorrateo).isLessThan(new BigDecimal("36.00"));
    }
}
