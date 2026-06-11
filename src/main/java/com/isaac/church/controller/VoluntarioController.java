package com.isaac.church.controller;

import com.isaac.church.dto.VoluntarioRequest;
import com.isaac.church.entity.Voluntario;
import com.isaac.church.service.VoluntarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de voluntários.
 *
 * DUPLA PROTEÇÃO em cada endpoint (Camadas 1 + 2):
 *
 *  POST   /api/voluntarios      → público (sem proteção)
 *  GET    /api/voluntarios      → C1: hasRole(ADMIN)  |  C2: @PreAuthorize(ADMIN)
 *  GET    /api/voluntarios/{id} → C1: hasRole(ADMIN)  |  C2: @PreAuthorize(ADMIN)
 *  DELETE /api/voluntarios/{id} → C1: hasRole(ADMIN)  |  C2: @PreAuthorize(ADMIN)
 *
 * O VoluntarioService aplica ainda a Camada 3 nos mesmos métodos.
 */
@RestController
@RequestMapping("/api/voluntarios")
public class VoluntarioController {

    private final VoluntarioService voluntarioService;

    public VoluntarioController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    // ── PÚBLICO ───────────────────────────────────────────────────────────────
    // C1 (SecurityConfig): permitAll() para POST /api/voluntarios
    // C2 (@PreAuthorize): não aplicada — intencionalmente público
    @PostMapping
    public ResponseEntity<?> inscrever(@Valid @RequestBody VoluntarioRequest request) {
        try {
            Voluntario salvo = voluntarioService.inscrever(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Inscrição realizada com sucesso!", "id", salvo.getId()));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // ── ADMIN — lista todos ───────────────────────────────────────────────────
    // C1 (SecurityConfig): hasRole("ADMIN") para GET /api/voluntarios
    // C2 (@PreAuthorize): reforça hasRole("ADMIN") no método
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Voluntario>> listar() {
        return ResponseEntity.ok(voluntarioService.listarTodos());
    }

    // ── ADMIN — busca por ID ──────────────────────────────────────────────────
    // C1 (SecurityConfig): hasRole("ADMIN") para GET /api/voluntarios/**
    // C2 (@PreAuthorize): reforça hasRole("ADMIN") no método
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return voluntarioService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Voluntário não encontrado")));
    }

    // ── ADMIN — remove ────────────────────────────────────────────────────────
    // C1 (SecurityConfig): hasRole("ADMIN") para DELETE /api/voluntarios/**
    // C2 (@PreAuthorize): reforça hasRole("ADMIN") no método
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            voluntarioService.remover(id);
            return ResponseEntity.ok(Map.of("message", "Inscrição removida com sucesso"));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}