package com.example.ProyectoSpring.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroUsuarioDTO {
    private String email;
    private String nombre;
    private String apellido;
    private String password;
    private String confirmPassword;
    private String pais;
    
    public boolean esValido() {
        return email != null && !email.isEmpty() &&
               nombre != null && !nombre.isEmpty() &&
               apellido != null && !apellido.isEmpty() &&
               password != null && !password.isEmpty() &&
               password.equals(confirmPassword);
    }
}
