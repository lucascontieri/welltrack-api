package com.welltrack.dto.lista;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosAtualizacaoLista(

        @NotNull
        UUID id_lista,

        String nomeLista) {
}

