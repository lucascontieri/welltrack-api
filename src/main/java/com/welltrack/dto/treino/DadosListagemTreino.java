package com.welltrack.dto.treino;

import com.welltrack.domain.treino.Treino;

import java.time.LocalDate;
import java.util.UUID;

public record DadosListagemTreino(
        UUID idTreino,
        String nomeTreino,
        LocalDate dataTreino) {

    public DadosListagemTreino(Treino treino) {
        this(treino.getIdTreino(),
                treino.getNomeTreino(),
                treino.getDataTreino());
    }
}

