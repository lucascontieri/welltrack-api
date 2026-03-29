package com.welltrack.dto.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosListagemTokenRecuperacao(
        UUID idToken,
        LocalDateTime expiracao,
        Boolean usado,
        Integer tentativas,
        String emailUsuario) {

    public DadosListagemTokenRecuperacao(TokenRecuperacao tokenRecuperacao) {
        this(tokenRecuperacao.getIdToken(),
                tokenRecuperacao.getExpiracao(),
                tokenRecuperacao.getUsado(),
                tokenRecuperacao.getTentativas(),
                tokenRecuperacao.getUsuario().getEmail());
    }
}

