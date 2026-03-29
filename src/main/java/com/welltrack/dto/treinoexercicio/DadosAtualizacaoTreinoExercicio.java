package com.welltrack.dto.treinoexercicio;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosAtualizacaoTreinoExercicio(

        @NotNull
        UUID idTreino,

        @NotNull
        UUID idExercicio,

        Integer serieExercicio,

        Integer descanso,

        BigDecimal carga,

        Integer repeticoes,

        Integer ordem) {
}
