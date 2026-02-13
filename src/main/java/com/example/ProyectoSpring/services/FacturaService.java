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
    private final TaxService taxService;

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

        // Calcular impuestos según país del usuario
        BigDecimal tasa = taxService.obtenerTasaPorPais(suscripcion.getUsuario() != null ? suscripcion.getUsuario().getPais() : null);
        BigDecimal montoImpuesto = montoTotal.multiply(tasa).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal montoTotalConImpuesto = montoTotal.add(montoImpuesto);

        Factura factura = Factura.builder()
                .usuario(suscripcion.getUsuario())
                .suscripcion(suscripcion)
                .plan(suscripcion.getPlanActual())
                .montoBase(montoBase)
                .montoProrrateo(montoProrrateo)
                .montoTotal(montoTotalConImpuesto)
                .tasaImpuesto(tasa)
                .montoImpuesto(montoImpuesto)
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
        // Recupera facturas por usuario
        // Cargar entidad Usuario para filtrar
        com.example.ProyectoSpring.entities.Usuario usuario = new com.example.ProyectoSpring.entities.Usuario();
        usuario.setId(usuarioId);
        return facturaRepository.findByUsuario(usuario);
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

    public List<Factura> buscarFacturas(java.time.LocalDateTime desde, java.time.LocalDateTime hasta,
                                        java.math.BigDecimal minMonto, java.math.BigDecimal maxMonto) {
        if (desde != null && hasta != null) {
            return facturaRepository.findByFechaEmisionBetween(desde, hasta);
        }
        if (minMonto != null && maxMonto != null) {
            return facturaRepository.findByMontoTotalBetween(minMonto, maxMonto);
        }
        if (minMonto != null) {
            return facturaRepository.findByMontoTotalGreaterThanEqual(minMonto);
        }
        if (maxMonto != null) {
            return facturaRepository.findByMontoTotalLessThanEqual(maxMonto);
        }
        // Default: todas
        return facturaRepository.findAll();
    }

    /**
     * Busca facturas del usuario actual (filtra por usuario autenticado)
     */
    public List<Factura> buscarFacturasUsuario(com.example.ProyectoSpring.entities.Usuario usuario,
                                               java.time.LocalDateTime desde, java.time.LocalDateTime hasta,
                                               java.math.BigDecimal minMonto, java.math.BigDecimal maxMonto) {
        // Obtiene facturas del usuario
        List<Factura> facturas = facturaRepository.findByUsuario(usuario);
        
        // Filtra por rango de fechas si se proporciona
        if (desde != null && hasta != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getFechaEmision() != null && 
                            !f.getFechaEmision().isBefore(desde) && 
                            !f.getFechaEmision().isAfter(hasta))
                    .toList();
        }
        
        // Filtra por rango de montos si se proporciona
        if (minMonto != null && maxMonto != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getMontoTotal().compareTo(minMonto) >= 0 && 
                            f.getMontoTotal().compareTo(maxMonto) <= 0)
                    .toList();
        } else if (minMonto != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getMontoTotal().compareTo(minMonto) >= 0)
                    .toList();
        } else if (maxMonto != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getMontoTotal().compareTo(maxMonto) <= 0)
                    .toList();
        }
        
        return facturas;
    }

    /**
     * Obtiene facturas vencidas del usuario actual
     */
    public List<Factura> obtenerFacturasVencidasUsuario(com.example.ProyectoSpring.entities.Usuario usuario) {
        List<Factura> facturas = facturaRepository.findByUsuarioAndEstado(usuario, EstadoFactura.PENDIENTE);
        return facturas.stream()
                .filter(f -> f.getFechaVencimiento() != null && f.getFechaVencimiento().isBefore(LocalDateTime.now()))
                .toList();
    }

    /**
     * Cancela todas las facturas pendientes de una suscripción
     * Se usa cuando se cambia de plan para anular la factura anterior
     */
    public void cancelarFacturasPendientesDeSuscripcion(Suscripcion suscripcion) {
        List<Factura> facturasPendientes = facturaRepository.findBySuscripcionAndEstado(suscripcion, EstadoFactura.PENDIENTE);
        for (Factura factura : facturasPendientes) {
            factura.setEstado(EstadoFactura.CANCELADA);
            facturaRepository.save(factura);
        }
    }
}
