package com.isaac.church.controller;

import com.isaac.church.entity.Usuario;
import com.isaac.church.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de usuários.
 *
 * Demonstra @PreAuthorize com expressões SpEL mais sofisticadas:
 *
 *  GET    /api/usuarios/me   → C1: authenticated()  |  C2: isAuthenticated()
 *  GET    /api/usuarios      → C1: hasRole(ADMIN)   |  C2: hasRole('ADMIN')
 *  DELETE /api/usuarios/{id} → C1: hasRole(ADMIN)   |  C2: hasRole('ADMIN') AND #id != principal.id
 *                                                         (ADMIN não pode remover a si mesmo)
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ── USER ou ADMIN — perfil próprio ────────────────────────────────────────
    // C1 (SecurityConfig): authenticated() para GET /api/usuarios/me
    // C2 (@PreAuthorize): isAuthenticated() — confirma que há um principal ativo
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> meuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "id",    u.getId(),
                        "email", u.getEmail(),
                        "role",  u.getRole()
                )))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuário não encontrado")));
    }

    // ── ADMIN — lista todos os usuários ───────────────────────────────────────
    // C1 (SecurityConfig): hasRole("ADMIN") para GET /api/usuarios
    // C2 (@PreAuthorize): reforça hasRole("ADMIN") no método
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // ── ADMIN — remove usuário (exceto a si mesmo) ────────────────────────────
    // C1 (SecurityConfig): hasRole("ADMIN") para DELETE /api/usuarios/**
    // C2 (@PreAuthorize com SpEL): ADMIN + garante que não está removendo a própria conta
    //    #id         → parâmetro @PathVariable do método
    //    principal   → objeto Usuario autenticado (implementa UserDetails)
    //    principal.id → getId() do Usuario logado
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and #id != principal.id")
    public ResponseEntity<?> remover(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Usuário removido com sucesso"));
    }
}