package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.services.SuscripcionService;
import com.example.ProyectoSpring.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar suscripciones
 */
@Controller
@RequestMapping("/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;
    private final UsuarioService usuarioService;

    @GetMapping("")
    public String listarSuscripciones(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        var usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        List<Suscripcion> suscripciones = suscripcionService.obtenerSuscripcionesDelUsuario(usuario.getId());
        model.addAttribute("suscripciones", suscripciones);
        return "suscripciones/lista";
    }

    @GetMapping("/{id}")
    public String verSuscripcion(@PathVariable Long id, Model model) {
        Suscripcion suscripcion = suscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        model.addAttribute("suscripcion", suscripcion);
        return "suscripciones/detalle";
    }

    @GetMapping("/{id}/cambiar-plan")
    public String mostrarFormularioCambioPlan(@PathVariable Long id, Model model) {
        Suscripcion suscripcion = suscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        
        model.addAttribute("suscripcion", suscripcion);
        model.addAttribute("niveles", NivelPlan.values());
        return "suscripciones/cambiar-plan";
    }

    @PostMapping("/{id}/cambiar-plan")
    public String cambiarPlan(
            @PathVariable Long id,
            @RequestParam NivelPlan nivel,
            Model model) {
        try {
            Suscripcion suscripcionActualizada = suscripcionService.cambiarPlan(id, nivel);
            model.addAttribute("mensaje", "Plan cambiado exitosamente");
            model.addAttribute("suscripcion", suscripcionActualizada);
            return "redirect:/suscripciones/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Suscripcion suscripcion = suscripcionService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
            model.addAttribute("suscripcion", suscripcion);
            model.addAttribute("niveles", NivelPlan.values());
            return "suscripciones/cambiar-plan";
        }
    }

    @GetMapping("/{id}/confirmar-cancelacion")
    public String mostrarConfirmacionCancelacion(@PathVariable Long id, Model model) {
        Suscripcion suscripcion = suscripcionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        model.addAttribute("suscripcion", suscripcion);
        return "suscripciones/confirmar-cancelacion";
    }

    @PostMapping("/{id}/confirmar-cancelacion")
    public String cancelarSuscripcion(@PathVariable Long id) {
        suscripcionService.cancelarSuscripcion(id);
        return "redirect:/suscripciones/" + id + "?cancelada=true";
    }
}
