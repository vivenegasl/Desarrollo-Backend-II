package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.security.jwt.CustomAccessDeniedHandler;
import com.petsalud.backendpetsalud.security.jwt.JwtAuthenticationEntryPoint;
import com.petsalud.backendpetsalud.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuracion central de seguridad con Spring Security 6:
 * - Autenticacion sin estado (Stateless) mediante JWT Bearer Tokens.
 * - Deshabilitacion de CSRF justificada por uso de Bearer Tokens en headers (inmune a ataques CSRF clasicos de cookies).
 * - Manejadores personalizados de 401 Unauthorized y 403 Forbidden.
 * - Control de acceso granular por roles (CLIENTE, EMPLEADO, GERENTE) con soporte para @PreAuthorize.
 * - Cabeceras de proteccion HTTP (XSS, Sniffing, FrameOptions para H2).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF: En arquitecturas REST stateless con JWT transmitidos via cabecera Authorization (Bearer),
                // el navegador no envia credenciales automaticamente en peticiones cross-site, neutralizando el vector de ataque CSRF.
                .csrf(AbstractHttpConfigurer::disable)

                // Habilitar CORS para permitir integracion segura con frontends clientes
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Gestion de sesion sin estado (stateless): el servidor no retiene sesiones HTTP ni cookies JSESSIONID
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Manejo controlado de excepciones de seguridad con respuestas JSON legibles
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                // Cabeceras de seguridad contra clickjacking y ataques de interpretacion MIME
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Permite visualizar la consola H2 en dev
                        .contentTypeOptions(withDefaults())
                )

                // Reglas de autorizacion HTTP declarativas (complementadas con @PreAuthorize en controladores)
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints publicos (sin autenticacion)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 2. Endpoints exclusivos de GERENTE / ADMIN (Gestion de usuarios, reportes globales)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/ventas").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/expedientes/**").hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("GERENTE", "ADMIN")

                        // 3. Endpoints de EMPLEADO / VETERINARIO / GERENTE (Gestion operativa, fichas medicas, stock)
                        .requestMatchers(HttpMethod.PUT, "/api/citas/*/estado").hasAnyRole("EMPLEADO", "GERENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/citas/veterinario/**").hasAnyRole("EMPLEADO", "GERENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/citas/entre-fechas").hasAnyRole("EMPLEADO", "GERENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/expedientes/**").hasAnyRole("EMPLEADO", "GERENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/expedientes/**").hasAnyRole("EMPLEADO", "GERENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/productos/stock-bajo").hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")

                        // 4. Todo el resto requiere autenticacion valida con JWT
                        .anyRequest().authenticated()
                )

                // Cargar detalles de usuario y agregar filtro JWT antes del filtro estandar de usuario y clave
                .userDetailsService(usuarioDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Factor de costo 12: optimo equilibrio entre costo computacional y resistencia a ataques de fuerza bruta
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
