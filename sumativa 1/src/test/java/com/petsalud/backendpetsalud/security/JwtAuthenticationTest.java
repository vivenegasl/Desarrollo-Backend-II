package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtAuthenticationTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "PetSaludSeguridad2024ClaveSuperSecretaConMasDe256BitsParaAlgoritmoHS256DuocUC";
    private final long expirationMs = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Debe generar un token JWT valido con claims correctos")
    void testGenerarYValidarToken() {
        String email = "cliente@petsalud.cl";
        Long userId = 1L;
        String nombre = "Juan Pérez";
        String rol = "CLIENTE";

        String token = jwtTokenProvider.generateToken(email, userId, nombre, rol);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getUsernameFromToken(token));
        assertEquals(rol, jwtTokenProvider.getRolFromToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Debe rechazar un token manipulado o con firma alterada")
    void testRechazarTokenManipulado() {
        String token = jwtTokenProvider.generateToken("cliente@petsalud.cl", 1L, "Juan", "CLIENTE");

        // Manipular el cuerpo del token alterando un caracter en la firma
        String tokenManipulado = token.substring(0, token.length() - 5) + "XYZ12";

        assertFalse(jwtTokenProvider.validateToken(tokenManipulado));
    }

    @Test
    @DisplayName("Debe rechazar un token expirado")
    void testRechazarTokenExpirado() {
        // Proveedor con tiempo de expiracion negativo (ya expirado)
        JwtTokenProvider expiredProvider = new JwtTokenProvider(secret, -1000);
        String tokenExpirado = expiredProvider.generateToken("cliente@petsalud.cl", 1L, "Juan", "CLIENTE");

        assertFalse(jwtTokenProvider.validateToken(tokenExpirado));
    }

    @Test
    @DisplayName("Debe rechazar tokens nulos o cadenas vacias")
    void testRechazarTokenVacio() {
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken("tokenInvalido.sinFormato.jwt"));
    }
}
