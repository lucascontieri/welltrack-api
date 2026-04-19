package com.welltrack.dto.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosCadastroTokenRecuperacao(

        @NotBlank
        String token,

        @NotNull
        LocalDateTime expiracao,

        @NotNull
        Boolean usado,

        @NotNull
        Integer tentativas,

        @NotNull
        LocalDateTime dataCriacao,

        @NotNull
        UUID idUsuario,

        /** Se null, assume {@link TipoTokenRecuperacao#RECUPERACAO_SENHA}. */
        TipoTokenRecuperacao tipo) {
}

