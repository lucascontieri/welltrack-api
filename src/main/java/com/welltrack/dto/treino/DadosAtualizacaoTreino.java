package com.welltrack.dto.treino;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record DadosAtualizacaoTreino(

        @NotNull
        UUID idTreino,

        String nomeTreino,

        LocalDate dataTreino) {
}

