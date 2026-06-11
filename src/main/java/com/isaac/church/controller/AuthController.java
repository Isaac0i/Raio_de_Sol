package com.isaac.church.controller;

import com.isaac.church.dto.AuthRequest;
import com.isaac.church.dto.AuthResponse;
import com.isaac.church.dto.RegisterRequest;
import com.isaac.church.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de autenticação — todas as rotas são PÚBLICAS.
 *
 * Camada 1 (SecurityConfig): permitAll() para /api/auth/**
 * Camada 2 (@PreAuthorize): não aplicada — intencionalmente aberto
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Cria uma conta e retorna o token JWT.
     * Body: { "email": "...", "senha": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     * Autentica e retorna o token JWT.
     * Body: { "email": "...", "senha": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        }
    }
}