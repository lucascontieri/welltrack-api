package com.welltrack.dto.alimento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosAtualizacaoAlimento(

        @NotNull
        UUID idAlimento,

        String nomeAlimento,

        @PositiveOrZero
        BigDecimal carboidrato,

        @PositiveOrZero
        BigDecimal proteina,

        @PositiveOrZero
        BigDecimal gordura,

        String unidadePadrao,

        @PositiveOrZero
        BigDecimal pesoPorcao,

        @PositiveOrZero
        BigDecimal calorias,

        String imagemAlimento) {
}

