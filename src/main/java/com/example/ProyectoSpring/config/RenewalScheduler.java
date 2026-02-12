package com.example.ProyectoSpring.config;

import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.services.SuscripcionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalScheduler {

    private final SuscripcionService suscripcionService;

    // Ejecutar diariamente a las 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void renovarSuscripcionesDiarias() {
        log.info("Scheduler: buscando suscripciones para renovar...");
        List<Suscripcion> pendientes = suscripcionService.obtenerSuscripcionesParaRenovar();
        for (Suscripcion s : pendientes) {
            try {
                if (Boolean.TRUE.equals(s.getAutoRenovar())) {
                    suscripcionService.renovarSuscripcion(s.getId());
                    log.info("Suscripción renovada automáticamente: {}", s.getId());
                }
            } catch (Exception e) {
                log.warn("Error renovando suscripción {}: {}", s.getId(), e.getMessage());
            }
        }
    }
}
