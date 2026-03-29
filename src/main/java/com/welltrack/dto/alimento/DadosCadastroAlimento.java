package com.welltrack.dto.alimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosCadastroAlimento(

        @NotBlank
        String nomeAlimento,

        @PositiveOrZero
        BigDecimal carboidrato,

        @PositiveOrZero
        BigDecimal proteina,

        @PositiveOrZero
        BigDecimal gordura,

        @NotBlank
        String unidadePadrao,

        @NotNull
        @PositiveOrZero
        BigDecimal pesoPorcao,

        @NotNull
        @PositiveOrZero
        BigDecimal calorias,

        String imagemAlimento,

        @NotNull
        UUID idUsuario) {
}

