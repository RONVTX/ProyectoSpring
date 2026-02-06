package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.dtos.SeleccionPlanDTO;
import com.example.ProyectoSpring.entities.Plan;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.enums.NivelPlan;
import com.example.ProyectoSpring.services.PlanService;
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
 * Controlador para gestionar planes y suscripciones
 */
@Controller
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final SuscripcionService suscripcionService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listarPlanes(Model model) {
        List<Plan> planes = planService.obtenerPlanesActivos();
        model.addAttribute("planes", planes);
        return "planes/lista";
    }

    @GetMapping("/seleccionar")
    public String seleccionarPlan(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Usuario usuario = usuarioService.obtenerPorEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        List<Plan> planes = planService.obtenerPlanesActivos();
        model.addAttribute("planes", planes);
        model.addAttribute("usuario", usuario);
        model.addAttribute("seleccionPlanDTO", new SeleccionPlanDTO());
        return "planes/seleccionar";
    }

    @PostMapping("/seleccionar")
    public String procesarSeleccionPlan(
            @RequestParam NivelPlan nivel,
            Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            Suscripcion suscripcion = suscripcionService.crearSuscripcion(usuario.getId(), nivel);
            return "redirect:/suscripciones/" + suscripcion.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            List<Plan> planes = planService.obtenerPlanesActivos();
            model.addAttribute("planes", planes);
            return "planes/seleccionar";
        }
    }
}
