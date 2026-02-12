package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Usuario;
import com.example.ProyectoSpring.services.FacturaService;
import com.example.ProyectoSpring.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar facturas
 */
@Controller
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listarFacturasPendientes(Model model,
                                          @RequestParam(required = false) String desde,
                                          @RequestParam(required = false) String hasta,
                                          @RequestParam(required = false) java.math.BigDecimal minMonto,
                                          @RequestParam(required = false) java.math.BigDecimal maxMonto) {

        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        java.time.LocalDateTime fDesde = null;
        java.time.LocalDateTime fHasta = null;
        try {
            if (desde != null) fDesde = java.time.LocalDate.parse(desde).atStartOfDay();
            if (hasta != null) fHasta = java.time.LocalDate.parse(hasta).atTime(23,59,59);
        } catch (Exception ignored) {}

        List<Factura> facturas = facturaService.buscarFacturasUsuario(usuario, fDesde, fHasta, minMonto, maxMonto);
        model.addAttribute("facturas", facturas);
        model.addAttribute("titulo", "Facturas");
        return "facturas/lista";
    }

    @GetMapping("/vencidas")
    public String listarFacturasVencidas(Model model) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        List<Factura> facturas = facturaService.obtenerFacturasVencidasUsuario(usuario);
        model.addAttribute("facturas", facturas);
        model.addAttribute("titulo", "Facturas Vencidas");
        return "facturas/lista";
    }

    @GetMapping("/{id}")
    public String verDetallFactura(@PathVariable Long id, Model model) {
        Factura factura = facturaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        model.addAttribute("factura", factura);
        return "facturas/detalle";
    }

    @PostMapping("/{id}/pagar")
    public String marcarComoPagada(@PathVariable Long id) {
        facturaService.marcarComoPagada(id);
        return "redirect:/facturas/" + id + "?pagada=true";
    }
}
