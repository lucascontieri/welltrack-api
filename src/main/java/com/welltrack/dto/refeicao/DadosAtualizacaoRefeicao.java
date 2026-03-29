package com.welltrack.dto.refeicao;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record DadosAtualizacaoRefeicao(

        @NotNull
        UUID idRefeicao,

        String nomeRefeicao,

        LocalTime horario,

        String tipoRecorrencia,

        String diasPersonalizados,

        String imagemRefeicao) {
}

