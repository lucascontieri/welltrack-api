package com.welltrack.dto.tarefa;

import com.welltrack.domain.tarefa.Tarefa;

import java.time.LocalDateTime;
import java.util.UUID;

public record DadosDetalhamentoTarefa(
        UUID idTarefa,
        String descricao,
        String status,
        LocalDateTime prazoMaximo,
        String imagemTarefa,
        UUID idLista,
        String nomeLista) {

    public DadosDetalhamentoTarefa(Tarefa tarefa) {
        this(tarefa.getIdTarefa(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrazoMaximo(),
                tarefa.getImagemTarefa(),
                tarefa.getLista().getId_lista(),
                tarefa.getLista().getNomeLista());
    }
}

