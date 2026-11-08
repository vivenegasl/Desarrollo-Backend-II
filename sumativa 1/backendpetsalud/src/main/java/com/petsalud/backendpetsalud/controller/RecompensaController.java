package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Recompensa;
import com.petsalud.backendpetsalud.service.RecompensaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
@Tag(name = "Programa de Recompensas", description = "Fidelizacion de clientes y acumulacion de puntos")
@SecurityRequirement(name = "bearerAuth")
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE', 'ADMIN')")
    @Operation(summary = "Asignar recompensa a usuario", description = "Operacion para que empleados o administracion asignen puntos o beneficios")
    public ResponseEntity<Recompensa> agregarRecompensa(@PathVariable Long usuarioId, @RequestBody Recompensa recompensa) {
        Recompensa nuevaRecompensa = recompensaService.agregarRecompensa(usuarioId, recompensa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRecompensa);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @Operation(summary = "Obtener detalle de recompensa", description = "Consulta de una recompensa por su ID")
    public ResponseEntity<Recompensa> obtenerRecompensaPorId(@PathVariable Long id) {
        return recompensaService.obtenerRecompensaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @Operation(summary = "Listar recompensas por usuario", description = "Permite a clientes o personal consultar los beneficios activos")
    public ResponseEntity<List<Recompensa>> listarRecompensasPorUsuario(@PathVariable Long usuarioId) {
        List<Recompensa> recompensas = recompensaService.listarRecompensasPorUsuario(usuarioId);
        return ResponseEntity.ok(recompensas);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @Operation(summary = "Eliminar recompensa", description = "Exclusivo de Gerencia para dar de baja beneficios")
    public ResponseEntity<Void> eliminarRecompensa(@PathVariable Long id) {
        recompensaService.eliminarRecompensa(id);
        return ResponseEntity.noContent().build();
    }
}
