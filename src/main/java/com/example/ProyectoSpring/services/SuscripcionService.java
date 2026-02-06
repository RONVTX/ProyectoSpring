package com.example.ProyectoSpring.services;

import com.example.ProyectoSpring.entities.*;
import com.example.ProyectoSpring.enums.EstadoSuscripcion;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.repositories.SuscripcionRepository;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import com.example.ProyectoSpring.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar suscripciones
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final FacturaService facturaService;

    /**
     * Crea una nueva suscripción para un usuario
     */
    public Suscripcion crearSuscripcion(Long usuarioId, NivelPlan nivelPlan) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Plan plan = planRepository.findByNivel(nivelPlan)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

        // Verificar que no tenga una suscripción activa
        Optional<Suscripcion> suscripcionExistente = suscripcionRepository
                .findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA);

        if (suscripcionExistente.isPresent()) {
            throw new IllegalArgumentException("El usuario ya tiene una suscripción activa");
        }

        Suscripcion suscripcion = Suscripcion.builder()
                .usuario(usuario)
                .planActual(plan)
                .estado(EstadoSuscripcion.ACTIVA)
                .autoRenovar(true)
                .build();

        suscripcion = suscripcionRepository.save(suscripcion);

        // Generar factura inicial
        facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        return suscripcion;
    }

    /**
     * Obtiene la suscripción activa de un usuario
     */
    public Optional<Suscripcion> obtenerSuscripcionActiva(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return suscripcionRepository.findByUsuarioAndEstado(usuario, EstadoSuscripcion.ACTIVA);
    }

    /**
     * Obtiene una suscripción por ID
     */
    public Optional<Suscripcion> obtenerPorId(Long id) {
        return suscripcionRepository.findById(id);
    }

    /**
     * Obtiene todas las suscripciones de un usuario
     */
    public List<Suscripcion> obtenerSuscripcionesDelUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return suscripcionRepository.findByUsuario(usuario);
    }

    /**
     * Cambia el plan de una suscripción activa
     * Si el nuevo plan es más caro, se genera una factura de prorrateo
     */
    public Suscripcion cambiarPlan(Long suscripcionId, NivelPlan nuevoNivel) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        if (!suscripcion.getEstado().equals(EstadoSuscripcion.ACTIVA)) {
            throw new IllegalArgumentException("Solo se puede cambiar el plan de una suscripción activa");
        }

        Plan planAnterior = suscripcion.getPlanActual();
        Plan planNuevo = planRepository.findByNivel(nuevoNivel)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

        suscripcion.setPlanActual(planNuevo);
        suscripcion = suscripcionRepository.save(suscripcion);

        // Generar factura de prorrateo si es necesario
        if (planNuevo.getPrecioMensual().compareTo(planAnterior.getPrecioMensual()) > 0) {
            facturaService.generarFacturaParaSuscripcion(suscripcion, planAnterior);
        }

        return suscripcion;
    }

    /**
     * Cancela una suscripción
     */
    public Suscripcion cancelarSuscripcion(Long suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        suscripcion.setEstado(EstadoSuscripcion.CANCELADA);
        suscripcion.setFechaCancelacion(LocalDateTime.now());
        suscripcion.setAutoRenovar(false);

        return suscripcionRepository.save(suscripcion);
    }

    /**
     * Obtiene todas las suscripciones que vencen hoy o antes
     * Estas deben ser renovadas
     */
    public List<Suscripcion> obtenerSuscripcionesParaRenovar() {
        return suscripcionRepository.findSuscripcionesParaRenovar(
                EstadoSuscripcion.ACTIVA,
                LocalDateTime.now()
        );
    }

    /**
     * Renueva una suscripción (genera nueva factura)
     */
    public Suscripcion renovarSuscripcion(Long suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        if (!suscripcion.getAutoRenovar()) {
            throw new IllegalArgumentException("La suscripción no está configurada para auto-renovación");
        }

        // Actualizar fechas de renovación
        suscripcion.setFechaRenovacion(LocalDateTime.now());
        suscripcion.setFechaProximoPago(LocalDateTime.now().plusDays(30));

        suscripcion = suscripcionRepository.save(suscripcion);

        // Generar nueva factura
        facturaService.generarFacturaParaSuscripcion(suscripcion, null);

        return suscripcion;
    }
}
