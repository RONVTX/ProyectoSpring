package com.example.ProyectoSpring.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio simple para obtener tasas de impuesto por país.
 * Valores de ejemplo. Ampliar según reglas reales.
 */
@Service
public class TaxService {

    private final Map<String, BigDecimal> tasas = new HashMap<>();

    public TaxService() {
        // Tasas ejemplo: país -> porcentaje (0.0 - 1.0)
        tasas.put("ES", new BigDecimal("0.21")); // España 21%
        tasas.put("US", BigDecimal.ZERO); // EEUU sin IVA
        tasas.put("MX", new BigDecimal("0.16")); // México 16%
        tasas.put("AR", new BigDecimal("0.21"));
    }

    public BigDecimal obtenerTasaPorPais(String pais) {
        if (pais == null) return BigDecimal.ZERO;
        return tasas.getOrDefault(pais.toUpperCase(), BigDecimal.ZERO);
    }
}
