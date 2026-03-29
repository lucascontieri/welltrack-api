package com.welltrack.dto.tarefa;

import com.welltrack.domain.tarefa.Tarefa;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosListagemTarefa(
        UUID idTarefa,
        String descricao,
        String status,
        LocalDateTime prazoMaximo) {

    public DadosListagemTarefa(Tarefa tarefa) {
        this(tarefa.getIdTarefa(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrazoMaximo());
    }
}

