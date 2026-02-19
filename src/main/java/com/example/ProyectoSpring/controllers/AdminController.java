
package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.repositories.FacturaRepository;
import com.example.ProyectoSpring.repositories.SuscripcionRepository;
import com.example.ProyectoSpring.repositories.UsuarioRepository;
import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.services.UsuarioService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Panel de auditoría básico para administradores
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EntityManager entityManager;
    private final FacturaRepository facturaRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    // Crear usuario (GET)
    @GetMapping("/auditoria/usuarios/crear")
    public String mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("registroUsuarioDTO", new RegistroUsuarioDTO());
        model.addAttribute("error", null);
        return "admin/usuario-crear";
    }

    // Crear usuario (POST)
    @PostMapping("/auditoria/usuarios/crear")
    public String crearUsuario(@ModelAttribute("registroUsuarioDTO") RegistroUsuarioDTO registroUsuarioDTO, Model model) {
        try {
            usuarioService.registrarUsuario(registroUsuarioDTO);
            return "redirect:/admin/auditoria";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registroUsuarioDTO", registroUsuarioDTO);
            model.addAttribute("error", e.getMessage());
            return "admin/usuario-crear";
        }
    }

    // CRUD Auditoría
    @GetMapping("/auditoria")
    public String verAuditoria(Model model) {
        List<Factura> facturas = facturaRepository.findAll();
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        model.addAttribute("facturas", facturas);
        model.addAttribute("suscripciones", suscripciones);
        return "admin/auditoria";
    }

    @GetMapping("/auditoria/suscripciones")
    public String listarSuscripciones(Model model) {
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        model.addAttribute("suscripciones", suscripciones);
        return "admin/suscripciones-lista";
    }

    @GetMapping("/auditoria/suscripciones/{id}")
    public String verSuscripcion(@PathVariable Long id, Model model) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        model.addAttribute("suscripcion", suscripcion);
        return "admin/suscripcion-detalle";
    }

    @GetMapping("/auditoria/suscripciones/{id}/editar")
    public String editarSuscripcion(@PathVariable Long id, Model model) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        model.addAttribute("suscripcion", suscripcion);
        return "admin/suscripcion-editar";
    }

    @PostMapping("/auditoria/suscripciones/{id}/editar")
    public String actualizarSuscripcion(@PathVariable Long id, @ModelAttribute Suscripcion suscripcion) {
        suscripcion.setId(id);
        suscripcionRepository.save(suscripcion);
        return "redirect:/admin/auditoria/suscripciones/{id}";
    }

    @PostMapping("/auditoria/suscripciones/{id}/eliminar")
    public String eliminarSuscripcion(@PathVariable Long id) {
        suscripcionRepository.deleteById(id);
        return "redirect:/admin/auditoria/suscripciones";
    }

    // Historial de suscripción por usuario
    @GetMapping("/auditoria/historial/{usuarioId}")
    public String verHistorialSuscripcion(@PathVariable Long usuarioId, Model model) {
        List<Suscripcion> historial = suscripcionRepository.findByUsuarioId(usuarioId);
        model.addAttribute("historial", historial);
        return "admin/historial-suscripcion";
    }
}
