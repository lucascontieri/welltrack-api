package com.welltrack.dto.treino;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record DadosCadastroTreino(

        @NotBlank
        String nomeTreino,

        LocalDate dataTreino,

        @NotNull
        UUID idUsuario) {
}

