package com.petsalud.backendpetsalud.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoleBasedAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtenerToken(String email, String password) throws Exception {
        LoginRequest login = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    @DisplayName("Endpoint publico /api/productos debe ser accesible sin autenticacion")
    void testAccesoPublicoProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint protegido sin token debe retornar 401 Unauthorized con formato JSON")
    void testAccesoSinAutenticacionRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Login con credenciales invalidas debe retornar 401 Unauthorized")
    void testLoginInvalidoRetorna401() throws Exception {
        LoginRequest badLogin = new LoginRequest("cliente@petsalud.cl", "PasswordIncorrecta!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Login exitoso debe retornar token JWT y datos de perfil")
    void testLoginExitoso() throws Exception {
        LoginRequest login = new LoginRequest("cliente@petsalud.cl", "Cliente123!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    @DisplayName("Rol CLIENTE intentando acceder a /api/usuarios (exclusivo GERENTE) debe retornar 403 Forbidden")
    void testClienteAccediendoAUsuariosRetorna403() throws Exception {
        String tokenCliente = obtenerToken("cliente@petsalud.cl", "Cliente123!");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @DisplayName("Rol EMPLEADO intentando acceder a /api/usuarios (exclusivo GERENTE) debe retornar 403 Forbidden")
    void testEmpleadoAccediendoAUsuariosRetorna403() throws Exception {
        String tokenEmpleado = obtenerToken("empleado@petsalud.cl", "Empleado123!");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenEmpleado))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Rol GERENTE accediendo a /api/usuarios debe retornar 200 OK con la lista de usuarios")
    void testGerenteAccediendoAUsuariosRetorna200() throws Exception {
        String tokenGerente = obtenerToken("gerente@petsalud.cl", "Gerente123!");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenGerente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Rol GERENTE accediendo al reporte global de ventas /api/ventas debe retornar 200 OK")
    void testGerenteAccediendoAVentasRetorna200() throws Exception {
        String tokenGerente = obtenerToken("gerente@petsalud.cl", "Gerente123!");

        mockMvc.perform(get("/api/ventas")
                        .header("Authorization", "Bearer " + tokenGerente))
                .andExpect(status().isOk());
    }
}
