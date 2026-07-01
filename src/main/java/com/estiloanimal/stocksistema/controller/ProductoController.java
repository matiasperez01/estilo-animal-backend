package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/codigo/{codigoBarra}")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigoBarra) {
        return ResponseEntity.ok(productoService.buscarPorCodigoBarra(codigoBarra));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<Producto>> listarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(productoService.listarPorProveedor(proveedorId));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Producto>> listarBajoStock() {
        return ResponseEntity.ok(productoService.listarProductosBajoStock());
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<Producto>> listarDestacados() {
        return ResponseEntity.ok(productoService.listarDestacados());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizar(id, producto));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}