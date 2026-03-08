package com.welltrack.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DadosAtualizacaoUsuario(

        @NotNull
        UUID idUsuario,

        String nome,

        LocalDate dataNascimento,

        BigDecimal peso,

        BigDecimal altura,

        String email,

        String logradouro,

        Integer numero,

        String complemento,

        String bairro,

        String cidade,

        String estado,

        String cep,

        String imagemUsuario) {

}
