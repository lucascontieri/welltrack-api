package com.welltrack.dto.alimento;

import com.welltrack.domain.alimento.Alimento;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosDetalhamentoAlimento(
        UUID idAlimento,
        String nomeAlimento,
        BigDecimal carboidrato,
        BigDecimal proteina,
        BigDecimal gordura,
        String unidadePadrao,
        BigDecimal pesoPorcao,
        BigDecimal calorias,
        String imagemAlimento,
        UUID idUsuario) {

    public DadosDetalhamentoAlimento(Alimento alimento) {
        this(alimento.getIdAlimento(),
                alimento.getNomeAlimento(),
                alimento.getCarboidrato(),
                alimento.getProteina(),
                alimento.getGordura(),
                alimento.getUnidadePadrao(),
                alimento.getPesoPorcao(),
                alimento.getCalorias(),
                alimento.getImagemAlimento(),
                alimento.getUsuario().getIdUsuario());
    }
}

