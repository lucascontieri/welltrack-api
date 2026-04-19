package com.welltrack.repository.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, UUID> {

    Page<TokenRecuperacao> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);

    Optional<TokenRecuperacao> findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
            String token, LocalDateTime agora, TipoTokenRecuperacao tipo);

    void deleteByUsuarioAndTipoAndUsadoFalse(Usuario usuario, TipoTokenRecuperacao tipo);
}

