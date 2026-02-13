package com.example.ProyectoSpring.config;

import com.example.ProyectoSpring.services.PlanService;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import com.example.ProyectoSpring.entities.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.ProyectoSpring.services.SuscripcionService;
import com.example.ProyectoSpring.enums.NivelPlan;
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
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuscripcionService suscripcionService;

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

        // Actualizar usuarios sin país a ES (por defecto)
        try {
            var usuariosSinPais = usuarioRepository.findAll().stream()
                    .filter(u -> u.getPais() == null || u.getPais().isEmpty())
                    .toList();
            if (!usuariosSinPais.isEmpty()) {
                for (Usuario u : usuariosSinPais) {
                    u.setPais("ES");
                    usuarioRepository.save(u);
                }
                log.info("Actualizados {} usuarios sin país asignado a ES", usuariosSinPais.size());
            }
        } catch (Exception e) {
            log.error("Error al actualizar países de usuarios", e);
        }

        // Crear usuario de prueba si no existe
        String emailPrueba = "test@local";
        if (!usuarioRepository.existsByEmail(emailPrueba)) {
            Usuario u = Usuario.builder()
                    .email(emailPrueba)
                    .nombre("Test")
                    .apellido("Usuario")
                    .password(passwordEncoder.encode("password"))
                    .pais("ES")
                    .isAdmin(false)
                    .build();
            usuarioRepository.save(u);
            log.info("Usuario de prueba creado: {} / password=password", emailPrueba);

            // Crear suscripción de prueba al plan BASIC para generar factura
            try {
                suscripcionService.crearSuscripcion(u.getId(), NivelPlan.BASIC);
                log.info("Suscripción de prueba creada para {}", emailPrueba);
            } catch (Exception ex) {
                log.warn("No se pudo crear suscripción de prueba: {}", ex.getMessage());
            }
        }

        // Crear usuario ADMIN si no existe
        String emailAdmin = "admin@local";
        if (!usuarioRepository.existsByEmail(emailAdmin)) {
            Usuario admin = Usuario.builder()
                    .email(emailAdmin)
                    .nombre("Admin")
                    .apellido("Sistema")
                    .password(passwordEncoder.encode("admin123"))
                    .pais("ES")
                    .isAdmin(true)
                    .build();
            usuarioRepository.save(admin);
            log.info("✅ USUARIO ADMIN CREADO - Email: {} / Contraseña: admin123", emailAdmin);
        }
    }
}
