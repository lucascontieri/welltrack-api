package com.welltrack.dto.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosDetalhamentoTokenRecuperacao(
        UUID idToken,
        String token,
        LocalDateTime expiracao,
        Boolean usado,
        Integer tentativas,
        LocalDateTime dataCriacao,
        UUID idUsuario,
        String emailUsuario) {

    public DadosDetalhamentoTokenRecuperacao(TokenRecuperacao tokenRecuperacao) {
        this(tokenRecuperacao.getIdToken(),
                tokenRecuperacao.getToken(),
                tokenRecuperacao.getExpiracao(),
                tokenRecuperacao.getUsado(),
                tokenRecuperacao.getTentativas(),
                tokenRecuperacao.getDataCriacao(),
                tokenRecuperacao.getUsuario().getIdUsuario(),
                tokenRecuperacao.getUsuario().getEmail());
    }
}

