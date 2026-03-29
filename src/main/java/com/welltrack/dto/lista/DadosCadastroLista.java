package com.welltrack.dto.lista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosCadastroLista(

        @NotBlank
        String nomeLista,

        @NotNull
        UUID idUsuario) {
}

