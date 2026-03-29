package com.welltrack.repository.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, UUID> {

    Page<TokenRecuperacao> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

