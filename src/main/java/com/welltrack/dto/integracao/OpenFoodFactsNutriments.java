package com.welltrack.dto.integracao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenFoodFactsNutriments(
        @JsonProperty("carbohydrates_100g") Double carbohydrates100g,
        @JsonProperty("proteins_100g") Double proteins100g,
        @JsonProperty("fat_100g") Double fat100g,
        @JsonProperty("energy-kcal_100g") Double energyKcal100g
) {
}
