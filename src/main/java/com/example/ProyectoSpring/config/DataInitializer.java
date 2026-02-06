package com.example.ProyectoSpring.config;

import com.example.ProyectoSpring.services.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos de la aplicación
 * Se ejecuta automáticamente al iniciar Spring Boot
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PlanService planService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando datos de la aplicación...");
        log.info("DataInitializer: Llamando a planService.inicializarPlanes()");
        try {
            planService.inicializarPlanes();
            log.info("Planes inicializados correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar planes", e);
            // No relanzar la excepción para que el servidor no se cierre
        }
    }
}
