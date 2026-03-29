package com.welltrack.dto.refeicaoalimento;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosAtualizacaoRefeicaoAlimento(
        @NotNull
        UUID idRefeicao,

        @NotNull
        UUID idAlimento,

        BigDecimal quantidade,

        String unidadeMedida
) {
}
