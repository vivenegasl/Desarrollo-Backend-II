package com.petsalud.backendpetsalud.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI petSaludOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clínica PetSalud - API REST Backend")
                        .description("API REST para la gestión integral de Clínica PetSalud. Incluye autenticación con JWT (JJWT 0.12.6), control de acceso basado en roles (RBAC: CLIENTE, EMPLEADO, GERENTE), agendamiento de citas, expedientes médicos, inventario, ventas y sistema de recompensas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo PetSalud")
                                .email("contacto@petsalud.cl"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese su token JWT generado mediante el endpoint /api/auth/login")));
    }
}
