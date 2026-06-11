package com.isaac.church.dto;

/**
 * Resposta de login/register
 * Response: { "token": "eyJhbGc..." }
 */
public class AuthResponse {
    private String token;

    public AuthResponse(String token) { this.token = token; }

    public String getToken()       { return token; }
    public void setToken(String v) { this.token = v; }
}