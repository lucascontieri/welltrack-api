package com.welltrack.dto.analisenutricional;

import java.math.BigDecimal;
import java.util.List;

public record DadosAnaliseNutricional(
        String nomeRefeicao,
        BigDecimal caloriasTotal,
        BigDecimal pesoTotal,
        BigDecimal carboidratoTotal,
        BigDecimal proteinaTotal,
        BigDecimal gorduraTotal,
        List<ItemNutricional> itens
) {
}
