package com.isaac.church.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Espelha exatamente os campos do formulário HTML do front-end:
 *   nomeCompleto  → input#nomeCompleto
 *   email         → input#emailContato
 *   telefone      → input#telefoneContato
 *   areaInteresse → select#areaInteresse
 *   mensagem      → textarea#mensagemVoluntario
 */
public class VoluntarioRequest {

    @NotBlank(message = "Nome completo é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Área de interesse é obrigatória")
    private String areaInteresse;

    private String mensagem; // opcional

    public String getNomeCompleto()        { return nomeCompleto; }
    public void setNomeCompleto(String v)  { this.nomeCompleto = v; }
    public String getEmail()               { return email; }
    public void setEmail(String v)         { this.email = v; }
    public String getTelefone()            { return telefone; }
    public void setTelefone(String v)      { this.telefone = v; }
    public String getAreaInteresse()       { return areaInteresse; }
    public void setAreaInteresse(String v) { this.areaInteresse = v; }
    public String getMensagem()            { return mensagem; }
    public void setMensagem(String v)      { this.mensagem = v; }
}