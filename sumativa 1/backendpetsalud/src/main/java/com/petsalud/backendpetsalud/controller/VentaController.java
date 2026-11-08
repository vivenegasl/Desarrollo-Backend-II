package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Venta;
import com.petsalud.backendpetsalud.service.VentaService;
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
@RequestMapping("/api/ventas")
@Tag(name = "Ventas y Facturación", description = "Procesamiento de ordenes de compra e historial de transacciones")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @Operation(summary = "Realizar venta / compra", description = "Genera una orden de venta con el detalle de productos seleccionados")
    public ResponseEntity<Venta> realizarVenta(
            @RequestParam Long usuarioId,
            @RequestBody List<Long> productosIds) {
        Venta nuevaVenta = ventaService.realizarVenta(usuarioId, productosIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @Operation(summary = "Obtener venta por ID", description = "Consulta del comprobante y detalle de una venta")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @Operation(summary = "Listar ventas por usuario", description = "Historial de compras personal del cliente")
    public ResponseEntity<List<Venta>> listarVentasPorUsuario(@PathVariable Long usuarioId) {
        List<Venta> ventas = ventaService.listarVentasPorUsuario(usuarioId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @Operation(summary = "Listar todas las ventas globales", description = "Reporte financiero consolidado reservado exclusivamente para la Gerencia")
    public ResponseEntity<List<Venta>> listarTodasLasVentas() {
        List<Venta> ventas = ventaService.listarTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
}
