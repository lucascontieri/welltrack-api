package com.welltrack.dto.tarefabloco;

import com.welltrack.domain.tarefabloco.TarefaBloco;

import java.util.UUID;

public record DadosDetalhamentoTarefaBloco(
        UUID idBloco,
        String tipo,
        String conteudo,
        Integer ordem,
        String propriedades,
        UUID idPai,
        UUID idTarefa,
        String descricaoTarefa) {

    public DadosDetalhamentoTarefaBloco(TarefaBloco tarefaBloco) {
        this(tarefaBloco.getIdBloco(),
                tarefaBloco.getTipo(),
                tarefaBloco.getConteudo(),
                tarefaBloco.getOrdem(),
                tarefaBloco.getPropriedades(),
                tarefaBloco.getPai() != null ? tarefaBloco.getPai().getIdBloco() : null,
                tarefaBloco.getTarefa().getIdTarefa(),
                tarefaBloco.getTarefa().getDescricao());
    }
}

