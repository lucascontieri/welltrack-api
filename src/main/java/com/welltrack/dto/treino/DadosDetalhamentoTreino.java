package com.welltrack.dto.treino;

import com.welltrack.domain.treino.Treino;

import java.time.LocalDate;
import java.util.UUID;

public record DadosDetalhamentoTreino(
        UUID idTreino,
        String nomeTreino,
        LocalDate dataTreino,
        UUID idUsuario) {

    public DadosDetalhamentoTreino(Treino treino) {
        this(treino.getIdTreino(),
                treino.getNomeTreino(),
                treino.getDataTreino(),
                treino.getUsuario().getIdUsuario());
    }
}

