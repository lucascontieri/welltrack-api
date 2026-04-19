package com.welltrack.dto.usuario;

import com.welltrack.domain.usuario.TipoUsuario;
import com.welltrack.domain.usuario.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DadosDetalhamentoUsuario(UUID idUsuario, String nome, String cpf, TipoUsuario tipoUsuario,
                                       LocalDate dataNascimento, BigDecimal peso, BigDecimal altura, String email,
                                       String senha, String celular, String logradouro, Integer numero,
                                       String complemento, String bairro, String cidade, String estado, String cep,
                                       Boolean emailVerificado, Boolean ativo, String imagemUsuario) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getIdUsuario(), usuario.getNome(), usuario.getCpf(), usuario.getTipoUsuario(), usuario.getDataNascimento(), usuario.getPeso(), usuario.getAltura(), usuario.getEmail(), null, usuario.getCelular(), usuario.getLogradouro(), usuario.getNumero(), usuario.getComplemento(), usuario.getBairro(), usuario.getCidade(), usuario.getEstado(), usuario.getCep(), usuario.getEmailVerificado(), usuario.getAtivo(), usuario.getImagemUsuario());
    }
}
