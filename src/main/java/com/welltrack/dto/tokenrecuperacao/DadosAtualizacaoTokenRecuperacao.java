package com.welltrack.dto.tokenrecuperacao;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosAtualizacaoTokenRecuperacao(

        @NotNull
        UUID idToken,

        String token,

        LocalDateTime expiracao,

        Boolean usado,

        Integer tentativas,

        LocalDateTime dataCriacao) {
}

