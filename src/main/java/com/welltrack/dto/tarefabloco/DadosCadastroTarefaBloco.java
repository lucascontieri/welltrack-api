package com.welltrack.dto.tarefabloco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosCadastroTarefaBloco(

        @NotBlank
        String tipo,

        @NotBlank
        String conteudo,

        @NotNull
        Integer ordem,

        String propriedades,

        UUID idPai,

        @NotNull
        UUID idTarefa) {
}

