package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.model.VarianteProducto;
import com.estiloanimal.stocksistema.service.VarianteProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos/{productoId}/variantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VarianteProductoController {

    private final VarianteProductoService varianteService;

    @GetMapping
    public ResponseEntity<List<VarianteProducto>> listar(@PathVariable Long productoId) {
        return ResponseEntity.ok(varianteService.listarPorProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<VarianteProducto> crear(
            @PathVariable Long productoId,
            @RequestBody VarianteProducto variante
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(varianteService.guardar(productoId, variante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VarianteProducto> actualizar(
            @PathVariable Long productoId,
            @PathVariable Long id,
            @RequestBody VarianteProducto variante
    ) {
        return ResponseEntity.ok(varianteService.actualizar(id, variante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long productoId,
            @PathVariable Long id
    ) {
        varianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}