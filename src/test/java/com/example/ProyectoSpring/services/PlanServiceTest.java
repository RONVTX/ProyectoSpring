package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PlanService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Plan")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private Plan planBasic;
    private Plan planPremium;
    private Plan planEnterprise;

    @BeforeEach
    void setUp() {
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

        planEnterprise = Plan.builder()
                .id(3L)
                .nivel(NivelPlan.ENTERPRISE)
                .nombre("Enterprise")
                .descripcion("Plan empresarial")
                .precioMensual(new BigDecimal("99.99"))
                .limiteFeatures(5000)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe obtener un plan por su nivel")
    void testObtenerPlanPorNivel() {
        // Arrange
        when(planRepository.findByNivel(NivelPlan.BASIC)).thenReturn(Optional.of(planBasic));

        // Act
        Optional<Plan> resultado = planService.obtenerPlanPorNivel(NivelPlan.BASIC);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNivel()).isEqualTo(NivelPlan.BASIC);
        assertThat(resultado.get().getPrecioMensual()).isEqualTo(new BigDecimal("9.99"));
    }

    @Test
    @DisplayName("Debe obtener un plan por ID")
    void testObtenerPlanPorId() {
        // Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.of(planBasic));

        // Act
        Optional<Plan> resultado = planService.obtenerPlanPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe obtener todos los planes activos")
    void testObtenerPlanesActivos() {
        // Arrange
        List<Plan> planes = Arrays.asList(planBasic, planPremium, planEnterprise);
        when(planRepository.findAll()).thenReturn(planes);

        // Act
        List<Plan> resultado = planService.obtenerPlanesActivos();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactly(planBasic, planPremium, planEnterprise);
    }

    @Test
    @DisplayName("Debe filtrar planes inactivos")
    void testObtenerPlanesActivosFiltrandoInactivos() {
        // Arrange
        planPremium.setActivo(false);
        List<Plan> planes = Arrays.asList(planBasic, planPremium, planEnterprise);
        when(planRepository.findAll()).thenReturn(planes);

        // Act
        List<Plan> resultado = planService.obtenerPlanesActivos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(planBasic, planEnterprise);
        assertThat(resultado).doesNotContain(planPremium);
    }

    @Test
    @DisplayName("Debe inicializar planes si no existen")
    void testInicializarPlanes() {
        // Arrange
        when(planRepository.existsByNivel(NivelPlan.BASIC)).thenReturn(false);
        when(planRepository.existsByNivel(NivelPlan.PREMIUM)).thenReturn(false);
        when(planRepository.existsByNivel(NivelPlan.ENTERPRISE)).thenReturn(false);
        when(planRepository.save(any(Plan.class)))
                .thenReturn(planBasic)
                .thenReturn(planPremium)
                .thenReturn(planEnterprise);

        // Act
        planService.inicializarPlanes();

        // Assert
        verify(planRepository, times(3)).existsByNivel(any());
        verify(planRepository, times(3)).save(any(Plan.class));
    }

    @Test
    @DisplayName("Debe retornar Optional vacío si el plan no existe")
    void testObtenerPlanNoExiste() {
        // Arrange
        when(planRepository.findByNivel(NivelPlan.BASIC)).thenReturn(Optional.empty());

        // Act
        Optional<Plan> resultado = planService.obtenerPlanPorNivel(NivelPlan.BASIC);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
