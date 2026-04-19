package com.welltrack.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosSolicitarRecuperacaoSenha(
        @NotBlank @Email String email) {
}
