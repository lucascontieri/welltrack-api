package com.welltrack.dto.refeicaoalimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosCadastroRefeicaoAlimento(
        @NotNull
        UUID idRefeicao,

        @NotNull
        UUID idAlimento,

        @NotNull
        @Positive
        BigDecimal quantidade,

        @NotBlank
        String unidadeMedida
) {
}
