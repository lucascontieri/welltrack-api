package com.welltrack.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosGoogleAuth(
        @NotBlank
        String token
) {
}
