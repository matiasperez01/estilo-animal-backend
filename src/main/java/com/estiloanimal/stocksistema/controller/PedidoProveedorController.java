package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.model.PedidoProveedor;
import com.estiloanimal.stocksistema.service.PedidoProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos-proveedor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoProveedorController {

    private final PedidoProveedorService pedidoProveedorService;

    @GetMapping
    public ResponseEntity<List<PedidoProveedor>> listarTodos() {
        return ResponseEntity.ok(pedidoProveedorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoProveedor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PedidoProveedor> crear(@RequestBody PedidoProveedor pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoProveedorService.crear(pedido));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoProveedor> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        PedidoProveedor.EstadoPedidoProveedor estado = PedidoProveedor.EstadoPedidoProveedor.valueOf(body.get("estado"));
        return ResponseEntity.ok(pedidoProveedorService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoProveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
