package com.welltrack.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroUsuario(

        @NotBlank
        String nome,

        String cpf,

        @NotNull
        LocalDate dataNascimento,

        BigDecimal peso,

        BigDecimal altura,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha,

        String celular,

        String logradouro,

        Integer numero,

        String complemento,

        String bairro,

        String cidade,

        String estado,

        @Pattern(regexp = "\\d{5}-?\\d{3}")
        String cep,

        String imagemUsuario) {
}
