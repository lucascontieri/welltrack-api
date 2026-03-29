package com.welltrack.dto.tarefabloco;

import com.welltrack.domain.tarefabloco.TarefaBloco;

import java.util.UUID;

public record DadosListagemTarefaBloco(
        UUID idBloco,
        String tipo,
        Integer ordem,
        String descricaoTarefa) {

    public DadosListagemTarefaBloco(TarefaBloco tarefaBloco) {
        this(tarefaBloco.getIdBloco(),
                tarefaBloco.getTipo(),
                tarefaBloco.getOrdem(),
                tarefaBloco.getTarefa().getDescricao());
    }
}

