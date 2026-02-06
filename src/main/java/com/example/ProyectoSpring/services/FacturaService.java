package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.enums.EstadoFactura;
import com.example.ProyectoSpring.repositories.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar facturas
 * Genera facturas automáticas y calcula prorrateos
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ProrrateoService prorrateoService;

    /**
     * Genera una factura para una suscripción
     * Si planAnterior no es null, calcula el prorrateo
     */
    public Factura generarFacturaParaSuscripcion(Suscripcion suscripcion, Plan planAnterior) {
        BigDecimal montoBase = suscripcion.getPlanActual().getPrecioMensual();
        BigDecimal montoProrrateo = BigDecimal.ZERO;
        String descripcion = "Suscripción plan " + suscripcion.getPlanActual().getNombre();

        // Si hay un plan anterior, calcular prorrateo
        if (planAnterior != null && 
            suscripcion.getPlanActual().getPrecioMensual()
                    .compareTo(planAnterior.getPrecioMensual()) > 0) {
            
            montoProrrateo = prorrateoService.calcularProrrateo(
                    suscripcion.getFechaProximoPago(),
                    planAnterior.getPrecioMensual(),
                    suscripcion.getPlanActual().getPrecioMensual()
            );
            descripcion += " (con prorrateo por cambio de plan)";
        }

        BigDecimal montoTotal = montoBase.add(montoProrrateo);

        Factura factura = Factura.builder()
                .usuario(suscripcion.getUsuario())
                .suscripcion(suscripcion)
                .plan(suscripcion.getPlanActual())
                .montoBase(montoBase)
                .montoProrrateo(montoProrrateo)
                .montoTotal(montoTotal)
                .descripcion(descripcion)
                .estado(EstadoFactura.PENDIENTE)
                .build();

        return facturaRepository.save(factura);
    }

    /**
     * Obtiene una factura por número
     */
    public Optional<Factura> obtenerPorNumero(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura);
    }

    /**
     * Obtiene todas las facturas de un usuario
     */
    public List<Factura> obtenerFacturasUsuario(Long usuarioId) {
        // Simplificado - en producción recuperarías el usuario
        return List.of();
    }

    /**
     * Obtiene facturas pendientes de pago
     */
    public List<Factura> obtenerFacturasPendientes() {
        return facturaRepository.findByEstado(EstadoFactura.PENDIENTE);
    }

    /**
     * Obtiene facturas vencidas
     */
    public List<Factura> obtenerFacturasVencidas() {
        return facturaRepository.findByEstadoAndFechaVencimientoBefore(
                EstadoFactura.PENDIENTE,
                LocalDateTime.now()
        );
    }

    /**
     * Marca una factura como pagada
     */
    public Factura marcarComoPagada(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDateTime.now());

        return facturaRepository.save(factura);
    }

    /**
     * Obtiene una factura por ID
     */
    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }
}
