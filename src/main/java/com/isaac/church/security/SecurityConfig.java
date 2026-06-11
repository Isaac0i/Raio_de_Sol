package com.isaac.church.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança.
 *
 * ESTRATÉGIA DE TRIPLA PROTEÇÃO (defense-in-depth):
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │ Camada 1 → SecurityConfig  (nível de rota HTTP)                      │
 * │   Bloqueia antes de o request chegar ao controller.                  │
 * │                                                                      │
 * │ Camada 2 → @PreAuthorize no Controller  (nível de método)            │
 * │   Garante a regra mesmo que a Camada 1 seja mal configurada.         │
 * │                                                                      │
 * │ Camada 3 → @PreAuthorize no Service  (nível de negócio)              │
 * │   Protege o service se ele for chamado de outros pontos da app.      │
 * └──────────────────────────────────────────────────────────────────────┘
 *
 * MAPA DE ACESSO:
 * ┌──────────────────────────────────┬──────────┬──────────┬───────┐
 * │ Rota                             │ Anônimo  │ USER     │ ADMIN │
 * ├──────────────────────────────────┼──────────┼──────────┼───────┤
 * │ GET  / , /index.html, *.css, *.js│ ✓        │ ✓        │ ✓     │
 * │ POST /api/auth/register          │ ✓        │ ✓        │ ✓     │
 * │ POST /api/auth/login             │ ✓        │ ✓        │ ✓     │
 * │ POST /api/voluntarios            │ ✓        │ ✓        │ ✓     │
 * │ GET  /api/voluntarios            │ ✗        │ ✗        │ ✓     │
 * │ GET  /api/voluntarios/{id}       │ ✗        │ ✗        │ ✓     │
 * │ DELETE /api/voluntarios/{id}     │ ✗        │ ✗        │ ✓     │
 * │ GET  /api/admin/**               │ ✗        │ ✗        │ ✓     │
 * │ GET  /api/usuarios/me            │ ✗        │ ✓        │ ✓     │
 * │ GET  /api/usuarios               │ ✗        │ ✗        │ ✓     │
 * │ DELETE /api/usuarios/{id}        │ ✗        │ ✗        │ ✓     │
 * │ GET  /h2-console/**              │ ✓ (dev)  │ ✓        │ ✓     │
 * └──────────────────────────────────┴──────────┴──────────┴───────┘
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter      = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ── CAMADA 1: Recursos estáticos do front-end ─────────────────
                        .requestMatchers(
                                "/", "/index.html", "/*.css", "/*.js", "/favicon.ico"
                        ).permitAll()

                        // ── CAMADA 1: Autenticação — pública ──────────────────────────
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // ── CAMADA 1: Inscrição de voluntário — pública ───────────────
                        .requestMatchers(HttpMethod.POST, "/api/voluntarios").permitAll()

                        // ── CAMADA 1: Leitura/exclusão de voluntários — só ADMIN ──────
                        .requestMatchers(HttpMethod.GET,    "/api/voluntarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/voluntarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/voluntarios/**").hasRole("ADMIN")

                        // ── CAMADA 1: Painel admin completo — só ADMIN ────────────────
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ── CAMADA 1: Perfil próprio — qualquer autenticado ───────────
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()

                        // ── CAMADA 1: Listagem e exclusão de usuários — só ADMIN ──────
                        .requestMatchers(HttpMethod.GET,    "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")

                        // ── CAMADA 1: H2 Console (desabilitar em produção) ────────────
                        .requestMatchers("/h2-console/**").permitAll()

                        // ── CAMADA 1: Qualquer outra rota — exige autenticação ─────────
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}