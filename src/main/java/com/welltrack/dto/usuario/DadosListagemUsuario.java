package com.welltrack.dto.usuario;

import com.welltrack.domain.usuario.Usuario;

import java.util.UUID;

public record DadosListagemUsuario(UUID idUsuario, String nome, String cpf, String email) {

    public DadosListagemUsuario(Usuario usuario) {
        this(usuario.getIdUsuario(), usuario.getNome(), usuario.getCpf(), usuario.getEmail());
    }
}
