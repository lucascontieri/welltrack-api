package com.welltrack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()) // Desativa CSRF (recomendado para APIs REST)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura autenticação sem estado (JWT)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // Permite acesso público ao login
                        .requestMatchers(HttpMethod.POST, "/login/google").permitAll() // Permite acesso público ao login com Google
                        .requestMatchers(HttpMethod.POST, "/login/solicitar-recuperacao-senha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login/redefinir-senha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuario").permitAll() // Permite cadastro público de usuários
                        .requestMatchers(HttpMethod.GET, "/usuario/confirmar-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuario/reenviar-verificacao").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/{idUsuario}").hasRole("ADMIN")
                        .anyRequest().authenticated() // Qualquer outra requisição precisa estar autenticada
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
