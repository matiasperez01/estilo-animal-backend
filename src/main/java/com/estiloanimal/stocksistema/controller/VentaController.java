package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.model.Venta;
import com.estiloanimal.stocksistema.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<Venta>> ventasDeHoy() {
        return ResponseEntity.ok(ventaService.listarVentasDeHoy());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> ventasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.listarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<Venta> registrar(@RequestBody Venta venta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Venta> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        Venta.EstadoVenta estado = Venta.EstadoVenta.valueOf(body.get("estado"));
        return ResponseEntity.ok(ventaService.actualizarEstado(id, estado));
    }
}