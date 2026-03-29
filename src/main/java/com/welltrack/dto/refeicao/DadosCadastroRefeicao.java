package com.welltrack.dto.refeicao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record DadosCadastroRefeicao(

        @NotBlank
        String nomeRefeicao,

        @NotNull
        LocalTime horario,

        @NotBlank
        String tipoRecorrencia,

        String diasPersonalizados,

        String imagemRefeicao,

        @NotNull
        UUID idUsuario) {
}

