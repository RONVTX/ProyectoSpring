package com.example.ProyectoSpring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio para calcular prorrateos
 * 
 * El prorrateo se calcula cuando un usuario cambia de un plan barato a uno caro
 * dentro del período de suscripción
 * 
 * Fórmula: (Precio nuevo - Precio anterior) * (Días restantes / 30)
 */
@Service
@RequiredArgsConstructor
public class ProrrateoService {

    private static final int DIAS_POR_MES = 30;
    private static final int ESCALA_DECIMALES = 2;

    /**
     * Calcula el prorrateo cuando se cambia de plan
     *
     * @param fechaProximoPago Fecha del próximo pago/renovación
     * @param precioPlanAnterior Precio del plan anterior (mensual)
     * @param precioPlanNuevo Precio del nuevo plan (mensual)
     * @return Monto del prorrateo a cobrar
     */
    public BigDecimal calcularProrrateo(LocalDateTime fechaProximoPago,
                                       BigDecimal precioPlanAnterior,
                                       BigDecimal precioPlanNuevo) {
        
        // Solo calcular prorrateo si el nuevo plan es más caro
        if (precioPlanNuevo.compareTo(precioPlanAnterior) <= 0) {
            return BigDecimal.ZERO;
        }

        // Obtener días restantes hasta el próximo pago
        long diasRestantes = calcularDiasRestantes(fechaProximoPago);
        
        if (diasRestantes <= 0) {
            return BigDecimal.ZERO;
        }

        // Diferencia de precio diaria
        BigDecimal diferenciaPrecio = precioPlanNuevo.subtract(precioPlanAnterior);
        
        // Precio diario del nuevo plan
        BigDecimal precioDiario = diferenciaPrecio.divide(
                new BigDecimal(DIAS_POR_MES),
                ESCALA_DECIMALES,
                RoundingMode.HALF_UP
        );

        // Prorrateo = precio diario * días restantes
        BigDecimal prorrateo = precioDiario.multiply(new BigDecimal(diasRestantes));
        
        // Redondear a 2 decimales
        return prorrateo.setScale(ESCALA_DECIMALES, RoundingMode.HALF_UP);
    }

    /**
     * Calcula los días restantes hasta la fecha especificada
     *
     * @param fecha Fecha objetivo
     * @return Número de días restantes (redondeado al día superior si hay horas)
     */
    public long calcularDiasRestantes(LocalDateTime fecha) {
        LocalDateTime ahora = LocalDateTime.now();
        
        if (fecha.isBefore(ahora)) {
            return 0;
        }

        // Contar días completos
        long dias = ChronoUnit.DAYS.between(ahora, fecha);
        
        // Si hay tiempo adicional en el día actual, contar como un día más
        if (ahora.plusDays(dias).isBefore(fecha)) {
            dias++;
        }

        return dias;
    }

    /**
     * Valida que los parámetros sean válidos para calcular prorrateo
     *
     * @param precioPlanAnterior Precio anterior
     * @param precioPlanNuevo Precio nuevo
     * @param fechaProximoPago Fecha de próximo pago
     * @return true si los parámetros son válidos
     */
    public boolean esValido(BigDecimal precioPlanAnterior,
                           BigDecimal precioPlanNuevo,
                           LocalDateTime fechaProximoPago) {
        return precioPlanAnterior != null &&
               precioPlanNuevo != null &&
               fechaProximoPago != null &&
               precioPlanAnterior.compareTo(BigDecimal.ZERO) >= 0 &&
               precioPlanNuevo.compareTo(BigDecimal.ZERO) >= 0 &&
               fechaProximoPago.isAfter(LocalDateTime.now());
    }
}
