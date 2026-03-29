package com.welltrack.dto.tarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosCadastroTarefa(

        @NotBlank
        String descricao,

        @NotBlank
        String status,

        LocalDateTime prazoMaximo,

        String imagemTarefa,

        @NotNull
        UUID idLista) {
}

