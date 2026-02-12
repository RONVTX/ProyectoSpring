package com.example.ProyectoSpring.controllers;

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

    @GetMapping("/auditoria")
    public String verAuditoria(Model model) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        // Obtener últimas revisiones de las entidades Factura y Suscripcion
        List<?> revisionesFacturas = reader.createQuery()
                .forRevisionsOfEntity(com.example.ProyectoSpring.entities.Factura.class, false, true)
                .setMaxResults(50)
                .getResultList();

        List<?> revisionesSuscripciones = reader.createQuery()
                .forRevisionsOfEntity(com.example.ProyectoSpring.entities.Suscripcion.class, false, true)
                .setMaxResults(50)
                .getResultList();

        model.addAttribute("revisionesFacturas", revisionesFacturas);
        model.addAttribute("revisionesSuscripciones", revisionesSuscripciones);
        return "admin/auditoria";
    }
}
