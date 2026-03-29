package com.welltrack.dto.refeicao;

import com.welltrack.domain.refeicao.Refeicao;

import java.time.LocalTime;
import java.util.UUID;

public record DadosListagemRefeicao(
        UUID idRefeicao,
        String nomeRefeicao,
        LocalTime horario,
        String tipoRecorrencia) {

    public DadosListagemRefeicao(Refeicao refeicao) {
        this(refeicao.getIdRefeicao(),
                refeicao.getNomeRefeicao(),
                refeicao.getHorario(),
                refeicao.getTipoRecorrencia());
    }
}

