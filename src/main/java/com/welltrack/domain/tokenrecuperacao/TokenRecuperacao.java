package com.welltrack.domain.tokenrecuperacao;

import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TokenRecuperacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idToken")
public class TokenRecuperacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_token")
    private UUID idToken;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiracao;

    @Column(nullable = false)
    private Boolean usado = false;

    @Column(nullable = false)
    private Integer tentativas = 0;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
