package com.welltrack.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosValidarCodigoRecuperacao(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 dígitos") String codigo) {
}
