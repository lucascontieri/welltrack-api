package com.welltrack.dto.refeicao;

import com.welltrack.domain.refeicao.Refeicao;

import java.time.LocalTime;
import java.util.UUID;

public record DadosDetalhamentoRefeicao(
        UUID idRefeicao,
        String nomeRefeicao,
        LocalTime horario,
        String tipoRecorrencia,
        String diasPersonalizados,
        String imagemRefeicao,
        UUID idUsuario) {

    public DadosDetalhamentoRefeicao(Refeicao refeicao) {
        this(refeicao.getIdRefeicao(),
                refeicao.getNomeRefeicao(),
                refeicao.getHorario(),
                refeicao.getTipoRecorrencia(),
                refeicao.getDiasPersonalizados(),
                refeicao.getImagemRefeicao(),
                refeicao.getUsuario().getIdUsuario());
    }
}

