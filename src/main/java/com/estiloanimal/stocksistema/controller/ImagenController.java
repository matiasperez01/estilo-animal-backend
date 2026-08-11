package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.model.ImagenProducto;
import com.estiloanimal.stocksistema.model.Producto;
import com.estiloanimal.stocksistema.repository.ImagenProductoRepository;
import com.estiloanimal.stocksistema.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImagenController {

    private final CloudinaryService cloudinaryService;
    private final ImagenProductoRepository imagenProductoRepository;

    @PostMapping("/subir")
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        try {
            String url = cloudinaryService.subirImagen(archivo);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/subir/{productoId}")
    public ResponseEntity<Map<String, Object>> subirImagenProducto(
            @PathVariable Long productoId,
            @RequestParam("archivo") MultipartFile archivo
    ) {
        try {
            String url = cloudinaryService.subirImagen(archivo);

            List<ImagenProducto> existentes = imagenProductoRepository
                    .findByProductoIdOrderByOrdenAsc(productoId);
            int orden = existentes.size();

            Producto producto = new Producto();
            producto.setId(productoId);

            ImagenProducto imagen = ImagenProducto.builder()
                    .url(url)
                    .orden(orden)
                    .producto(producto)
                    .build();

            imagenProductoRepository.save(imagen);

            return ResponseEntity.ok(Map.of(
                    "url", url,
                    "id", imagen.getId(),
                    "orden", (Object) orden
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/imagen/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long imagenId) {
        imagenProductoRepository.deleteById(imagenId);
        return ResponseEntity.noContent().build();
    }
}