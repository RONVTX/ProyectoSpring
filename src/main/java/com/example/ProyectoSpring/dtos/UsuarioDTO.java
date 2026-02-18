package com.example.ProyectoSpring.dtos;

import lombok.Data;

/**
 * DTO para actualizar información de usuario en configuración
 */
@Data
public class UsuarioDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String pais;
}