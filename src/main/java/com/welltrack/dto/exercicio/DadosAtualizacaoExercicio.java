package com.welltrack.dto.exercicio;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosAtualizacaoExercicio(

        @NotNull
        UUID idExercicio,

        String nomeExercicio,

        String imagemExercicio,

        String videoExercicio,

        UUID idGrupo) {
}

