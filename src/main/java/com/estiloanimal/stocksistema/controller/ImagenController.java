package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImagenController {

    private final CloudinaryService cloudinaryService;

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
}