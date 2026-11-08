package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.ErrorResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.dto.RegisterRequest;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.security.jwt.JwtTokenProvider;
import com.petsalud.backendpetsalud.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro de usuarios con JWT")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT", description = "Valida credenciales contra la BD y genera un token JWT firmado con HS256")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

            String token = jwtTokenProvider.generateToken(
                    authentication,
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getRol().name()
            );

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .tipo("Bearer")
                    .id(usuario.getId())
                    .email(usuario.getEmail())
                    .nombre(usuario.getNombre())
                    .apellido(usuario.getApellido())
                    .rol(usuario.getRol().name())
                    .expiresIn(jwtTokenProvider.getExpirationMs())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Unauthorized")
                    .message("Credenciales invalidas: correo o contrasena incorrectos.")
                    .path("/api/auth/login")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario en el sistema con contraseña cifrada en BCrypt y retorna su token JWT")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Usuario nuevoUsuario = Usuario.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .telefono(request.getTelefono())
                    .direccion(request.getDireccion())
                    .rol(request.getRol() != null ? request.getRol() : Rol.CLIENTE)
                    .build();

            Usuario guardado = usuarioService.registrarUsuario(nuevoUsuario);

            String token = jwtTokenProvider.generateToken(
                    guardado.getEmail(),
                    guardado.getId(),
                    guardado.getNombre(),
                    guardado.getRol().name()
            );

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .tipo("Bearer")
                    .id(guardado.getId())
                    .email(guardado.getEmail())
                    .nombre(guardado.getNombre())
                    .apellido(guardado.getApellido())
                    .rol(guardado.getRol().name())
                    .expiresIn(jwtTokenProvider.getExpirationMs())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Bad Request")
                    .message(ex.getMessage())
                    .path("/api/auth/register")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
