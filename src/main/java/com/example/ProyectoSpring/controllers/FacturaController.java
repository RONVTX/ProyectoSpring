package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.services.FacturaService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public String listarFacturasPendientes(Model model) {
        List<Factura> facturas = facturaService.obtenerFacturasPendientes();
        model.addAttribute("facturas", facturas);
        model.addAttribute("titulo", "Facturas Pendientes");
        return "facturas/lista";
    }

    @GetMapping("/vencidas")
    public String listarFacturasVencidas(Model model) {
        List<Factura> facturas = facturaService.obtenerFacturasVencidas();
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
