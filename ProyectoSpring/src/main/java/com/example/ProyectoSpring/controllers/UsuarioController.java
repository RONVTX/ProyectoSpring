package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar usuarios
 */
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroUsuarioDTO());
        return "usuarios/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute RegistroUsuarioDTO dto, Model model) {
        try {
            Usuario usuarioNuevo = usuarioService.registrarUsuario(dto);
            return "redirect:/planes/seleccionar";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registroDTO", dto);
            return "usuarios/registro";
        }
    }

    @GetMapping("/{id}")
    public String verPerfilUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuarios/perfil";
    }
}
