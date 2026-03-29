package com.welltrack.dto.exercicio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosCadastroExercicio(

        @NotBlank
        String nomeExercicio,

        String imagemExercicio,

        String videoExercicio,

        @NotNull
        UUID idGrupo,

        @NotNull
        UUID idUsuario) {
}

