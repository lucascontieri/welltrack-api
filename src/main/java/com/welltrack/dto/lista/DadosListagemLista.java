package com.welltrack.dto.lista;

import com.welltrack.domain.lista.Lista;

import java.util.UUID;

public record DadosListagemLista(
        UUID id_lista,
        String nomeLista) {

    public DadosListagemLista(Lista lista) {
        this(lista.getId_lista(),
                lista.getNomeLista());
    }
}

