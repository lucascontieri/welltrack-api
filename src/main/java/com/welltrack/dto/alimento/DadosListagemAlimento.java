package com.welltrack.dto.alimento;

import com.welltrack.domain.alimento.Alimento;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosListagemAlimento(
        UUID idAlimento,
        String nomeAlimento,
        BigDecimal calorias,
        String unidadePadrao,
        String imagemAlimento) {

    public DadosListagemAlimento(Alimento alimento) {
        this(alimento.getIdAlimento(),
                alimento.getNomeAlimento(),
                alimento.getCalorias(),
                alimento.getUnidadePadrao(),
                alimento.getImagemAlimento());
    }
}

