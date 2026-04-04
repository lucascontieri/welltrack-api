package com.welltrack.dto.alimento;

import java.math.BigDecimal;

public record DadosAlimentoPorCodigoDeBarras(
        String nomeAlimento,
        BigDecimal carboidrato,
        BigDecimal proteina,
        BigDecimal gordura,
        BigDecimal calorias,
        String unidadePadrao,
        BigDecimal pesoPorcao,
        String imagemAlimento
) {
}
