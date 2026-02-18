package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.dtos.RegistroUsuarioDTO;
import com.example.ProyectoSpring.dtos.UsuarioDTO;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestionar usuarios
 */
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuarios/perfil";
    }

    @GetMapping("/configuracion")
    public String mostrarConfiguracion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setPais(usuario.getPais());

        model.addAttribute("usuarioDTO", usuarioDTO);
        return "usuarios/configuracion";
    }

    @PostMapping("/configuracion")
    public String actualizarConfiguracion(@RequestParam String action,
                                        @ModelAttribute UsuarioDTO usuarioDTO,
                                        @RequestParam(required = false) String currentPassword,
                                        @RequestParam(required = false) String newPassword,
                                        @RequestParam(required = false) String confirmPassword,
                                        @RequestParam(required = false) String deletePassword,
                                        RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        try {
            switch (action) {
                case "updateProfile":
                    // Validar que el email no esté en uso por otro usuario
                    if (!usuario.getEmail().equals(usuarioDTO.getEmail()) && usuarioService.existeEmail(usuarioDTO.getEmail())) {
                        redirectAttributes.addFlashAttribute("error", "El email ya está en uso por otro usuario");
                        return "redirect:/usuarios/configuracion";
                    }
                    usuario.setNombre(usuarioDTO.getNombre());
                    usuario.setApellido(usuarioDTO.getApellido());
                    usuario.setEmail(usuarioDTO.getEmail());
                    usuario.setPais(usuarioDTO.getPais());
                    usuarioService.actualizar(usuario);
                    redirectAttributes.addFlashAttribute("exito", "Información actualizada correctamente");
                    break;

                case "changePassword":
                    if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
                        redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                        return "redirect:/usuarios/configuracion";
                    }
                    if (!newPassword.equals(confirmPassword)) {
                        redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                        return "redirect:/usuarios/configuracion";
                    }
                    usuario.setPassword(passwordEncoder.encode(newPassword));
                    usuarioService.actualizar(usuario);
                    redirectAttributes.addFlashAttribute("exito", "Contraseña cambiada correctamente");
                    break;

                case "updatePreferences":
                    // Aquí se implementarían las preferencias si se guardan en BD
                    redirectAttributes.addFlashAttribute("exito", "Preferencias guardadas correctamente");
                    break;

                case "deleteAccount":
                    if (!passwordEncoder.matches(deletePassword, usuario.getPassword())) {
                        redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta");
                        return "redirect:/usuarios/configuracion";
                    }
                    usuarioService.eliminarUsuario(usuario.getId());
                    return "redirect:/logout";

                default:
                    redirectAttributes.addFlashAttribute("error", "Acción no válida");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }

        return "redirect:/usuarios/configuracion";
    }
}
