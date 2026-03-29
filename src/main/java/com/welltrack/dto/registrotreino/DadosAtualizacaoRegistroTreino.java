package com.welltrack.dto.registrotreino;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record DadosAtualizacaoRegistroTreino(

        @NotNull
        UUID idRegistro,

        LocalDate dataExecucao,

        LocalTime horaEntrada,

        LocalTime horaSaida,

        Boolean concluido) {
}

