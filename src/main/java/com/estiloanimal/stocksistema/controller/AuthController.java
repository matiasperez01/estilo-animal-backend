package com.estiloanimal.stocksistema.controller;

import com.estiloanimal.stocksistema.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtService jwtService;
    private final String adminPassword;

    public AuthController(JwtService jwtService, @Value("${app.admin-password}") String adminPassword) {
        this.jwtService = jwtService;
        this.adminPassword = adminPassword;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || !password.equals(adminPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña incorrecta"));
        }
        String token = jwtService.generarToken("admin");
        return ResponseEntity.ok(Map.of("token", token));
    }
}
