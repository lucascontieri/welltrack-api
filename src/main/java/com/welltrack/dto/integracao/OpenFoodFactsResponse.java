package com.welltrack.dto.integracao;

public record OpenFoodFactsResponse(
        Integer status,
        OpenFoodFactsProduct product
) {
}
