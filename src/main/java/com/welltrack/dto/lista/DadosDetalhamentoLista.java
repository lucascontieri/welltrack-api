package com.welltrack.dto.lista;

import com.welltrack.domain.lista.Lista;

import java.util.UUID;

public record DadosDetalhamentoLista(
        UUID id_lista,
        String nomeLista,
        UUID idUsuario) {

    public DadosDetalhamentoLista(Lista lista) {
        this(lista.getId_lista(),
                lista.getNomeLista(),
                lista.getUsuario().getIdUsuario());
    }
}

