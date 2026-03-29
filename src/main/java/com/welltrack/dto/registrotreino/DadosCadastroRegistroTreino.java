package com.welltrack.dto.registrotreino;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record DadosCadastroRegistroTreino(

        @NotNull
        LocalDate dataExecucao,

        LocalTime horaEntrada,

        LocalTime horaSaida,

        @NotNull
        Boolean concluido,

        @NotNull
        UUID idTreino,

        @NotNull
        UUID idUsuario) {
}

