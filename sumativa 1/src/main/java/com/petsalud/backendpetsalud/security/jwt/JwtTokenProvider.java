package com.petsalud.backendpetsalud.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Componente para la generacion, firma y validacion de tokens JWT (JSON Web Token)
 * utilizando la especificacion JJWT 0.12.x y el algoritmo criptografico HMAC-SHA256 (HS256).
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:PetSaludSeguridad2024ClaveSuperSecretaConMasDe256BitsParaAlgoritmoHS256DuocUC}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Genera un token JWT a partir de los datos de autenticacion del usuario.
     */
    public String generateToken(Authentication authentication, Long userId, String nombre, String rol) {
        String email = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("nombre", nombre)
                .claim("rol", rol)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Sobrecarga para generacion directa con email y claims basicos.
     */
    public String generateToken(String email, Long userId, String nombre, String rol) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("nombre", nombre)
                .claim("rol", rol)
                .claim("authorities", "ROLE_" + rol)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrae el rol almacenado en el token JWT.
     */
    public String getRolFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("rol", String.class);
    }

    /**
     * Extrae el ID de usuario almacenado en el token.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        Object id = claims.get("userId");
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    /**
     * Valida la firma, estructura y vigencia temporal del token JWT.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            logger.warn("Firma del token JWT invalida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.warn("Estructura de token JWT malformada: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.warn("El token JWT ha expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.warn("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.warn("Token JWT vacio o nulo: {}", ex.getMessage());
        } catch (JwtException ex) {
            logger.warn("Error en validacion de token JWT: {}", ex.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
