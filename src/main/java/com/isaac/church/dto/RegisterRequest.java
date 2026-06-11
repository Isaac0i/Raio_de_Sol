package com.isaac.church.dto;

/**
 * POST /api/auth/register
 * Body: { "email": "...", "senha": "..." }
 */
public class RegisterRequest {
    private String email;
    private String senha;

    public String getEmail()       { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getSenha()       { return senha; }
    public void setSenha(String v) { this.senha = v; }
}