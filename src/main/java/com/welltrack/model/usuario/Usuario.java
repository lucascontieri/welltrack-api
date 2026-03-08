package com.welltrack.model.usuario;

import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Mapeando a entidade JPA 'Usuario' no banco de dados PostgreSQL na tabela 'tabelaUsuario'
@Entity(name = "Usuario")
@Table(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID idUsuario;

    private String nome;

    private String cpf;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario = TipoUsuario.USER;

    private LocalDate dataNascimento;

    private BigDecimal peso;

    private BigDecimal altura;

    private String email;

    private String senha;

    private String celular;

    private String logradouro;

    private Integer numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;

    private Boolean ativo;

    private String imagemUsuario;

    public Usuario(DadosCadastroUsuario dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.dataNascimento = dados.dataNascimento();
        this.peso = dados.peso();
        this.altura = dados.altura();
        this.email = dados.email();
        this.senha = dados.senha();
        this.celular = dados.celular();
        this.logradouro = dados.logradouro();
        this.numero = dados.numero();
        this.complemento = dados.complemento();
        this.bairro = dados.bairro();
        this.cidade = dados.cidade();
        this.estado = dados.estado();
        this.cep = dados.cep();
        this.imagemUsuario = dados.imagemUsuario();

    }

    public void atualizarInformacoes(@Valid DadosAtualizacaoUsuario dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }

        if (dados.dataNascimento() != null) {
            this.dataNascimento = dados.dataNascimento();
        }

        if (dados.peso() != null) {
            this.peso = dados.peso();
        }

        if (dados.altura() != null) {
            this.altura = dados.altura();
        }

        if (dados.email() != null) {
            this.email = dados.email();
        }

        if (dados.logradouro() != null) {
            this.logradouro = dados.logradouro();
        }

        if (dados.numero() != null) {
            this.numero = dados.numero();
        }

        if (dados.complemento() != null) {
            this.complemento = dados.complemento();
        }

        if (dados.bairro() != null) {
            this.bairro = dados.bairro();
        }

        if (dados.cidade() != null) {
            this.cidade = dados.cidade();
        }

        if (dados.estado() != null) {
            this.estado = dados.estado();
        }

        if (dados.cep() != null) {
            this.cep = dados.cep();
        }

        if (dados.imagemUsuario() != null) {
            this.imagemUsuario = dados.imagemUsuario();
        }
    }

    public void inativar() {
        this.ativo = false;
    }
}
