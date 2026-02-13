package com.example.ProyectoSpring.controllers;

import com.example.ProyectoSpring.entities.Factura;
import com.example.ProyectoSpring.entities.Suscripcion;
import com.example.ProyectoSpring.repositories.FacturaRepository;
import com.example.ProyectoSpring.repositories.SuscripcionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/auditoria")
    public String verAuditoria(Model model) {
        // Obtener todas las facturas
        List<Factura> facturas = facturaRepository.findAll();
        
        // Obtener todas las suscripciones
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        
        model.addAttribute("facturas", facturas);
        model.addAttribute("suscripciones", suscripciones);
        return "admin/auditoria";
    }
}
