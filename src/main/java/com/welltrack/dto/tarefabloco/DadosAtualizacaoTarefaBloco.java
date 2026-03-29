package com.welltrack.dto.tarefabloco;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosAtualizacaoTarefaBloco(

        @NotNull
        UUID idBloco,

        String tipo,

        String conteudo,

        Integer ordem,

        String propriedades,

        UUID idPai) {
}

