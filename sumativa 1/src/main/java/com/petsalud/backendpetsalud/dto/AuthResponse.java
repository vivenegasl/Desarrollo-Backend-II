package com.petsalud.backendpetsalud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
    private Long expiresIn;
}
