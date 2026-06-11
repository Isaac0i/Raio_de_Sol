package com.isaac.church.service;

import com.isaac.church.dto.VoluntarioRequest;
import com.isaac.church.entity.Voluntario;
import com.isaac.church.repository.VoluntarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service de voluntários.
 *
 * CAMADA 3 de segurança: @PreAuthorize nos métodos de leitura/exclusão.
 * Garante que, mesmo que outro controller ou scheduler chame esses métodos
 * diretamente, a regra de role será respeitada.
 */
@Service
public class VoluntarioService {

    private final VoluntarioRepository voluntarioRepository;

    public VoluntarioService(VoluntarioRepository voluntarioRepository) {
        this.voluntarioRepository = voluntarioRepository;
    }

    // ── Público: sem restrição de role ────────────────────────────────────────
    public Voluntario inscrever(VoluntarioRequest request) {
        if (voluntarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este e-mail já está inscrito como voluntário");
        }
        var v = new Voluntario();
        v.setNomeCompleto(request.getNomeCompleto());
        v.setEmail(request.getEmail());
        v.setTelefone(request.getTelefone());
        v.setAreaInteresse(request.getAreaInteresse());
        v.setMensagem(request.getMensagem());
        return voluntarioRepository.save(v);
    }

    // ── CAMADA 3: @PreAuthorize como barreira final ───────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Voluntario> buscarPorId(Long id) {
        return voluntarioRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void remover(Long id) {
        if (!voluntarioRepository.existsById(id)) {
            throw new RuntimeException("Voluntário não encontrado com id: " + id);
        }
        voluntarioRepository.deleteById(id);
    }
}