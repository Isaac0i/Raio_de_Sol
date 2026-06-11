package com.isaac.church.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "voluntarios")
public class Voluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Campo "nomeCompleto" do formulário HTML */
    @NotBlank(message = "Nome completo é obrigatório")
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    /** Campo "emailContato" do formulário HTML */
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** Campo "telefoneContato" do formulário HTML */
    @NotBlank(message = "Telefone é obrigatório")
    @Column(name = "telefone", nullable = false)
    private String telefone;

    /**
     * Campo "areaInteresse" do formulário HTML.
     * Valores: louvor | criancas | jovens | social | recepcao | midia | intercessao | outro
     */
    @NotBlank(message = "Área de interesse é obrigatória")
    @Column(name = "area_interesse", nullable = false)
    private String areaInteresse;

    /** Campo "mensagemVoluntario" do formulário HTML — opcional */
    @Column(name = "mensagem", columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_inscricao", nullable = false, updatable = false)
    private LocalDateTime dataInscricao;

    @PrePersist
    protected void onCreate() {
        this.dataInscricao = LocalDateTime.now();
    }

    // ─── Getters e Setters ────────────────────────────────────────────────────

    public Long getId()                        { return id; }
    public String getNomeCompleto()            { return nomeCompleto; }
    public void setNomeCompleto(String v)      { this.nomeCompleto = v; }
    public String getEmail()                   { return email; }
    public void setEmail(String v)             { this.email = v; }
    public String getTelefone()                { return telefone; }
    public void setTelefone(String v)          { this.telefone = v; }
    public String getAreaInteresse()           { return areaInteresse; }
    public void setAreaInteresse(String v)     { this.areaInteresse = v; }
    public String getMensagem()                { return mensagem; }
    public void setMensagem(String v)          { this.mensagem = v; }
    public LocalDateTime getDataInscricao()    { return dataInscricao; }
}