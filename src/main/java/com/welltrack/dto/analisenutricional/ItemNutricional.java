package com.welltrack.dto.analisenutricional;

import java.math.BigDecimal;

public record ItemNutricional(
        String nome,
        BigDecimal quantidade,
        BigDecimal calorias,
        BigDecimal carboidrato,
        BigDecimal proteina,
        BigDecimal gordura
) {
}
