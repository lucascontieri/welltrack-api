package com.welltrack.dto.integracao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenFoodFactsProduct(
        @JsonProperty("product_name") String productName,
        @JsonProperty("image_url") String imageUrl,
        OpenFoodFactsNutriments nutriments
) {
}
