package com.welltrack.dto.tarefa;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosAtualizacaoTarefa(

        @NotNull
        UUID idTarefa,

        String descricao,

        String status,

        LocalDateTime prazoMaximo,

        String imagemTarefa) {
}

