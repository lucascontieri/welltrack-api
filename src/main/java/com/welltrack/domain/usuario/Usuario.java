package com.welltrack.domain.usuario;

import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

// Mapeando a entidade JPA 'Usuario' no banco de dados PostgreSQL na tabela 'tabelaUsuario'
@Entity(name = "Usuario")
@Table(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")

public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID idUsuario;

    private String nome;

    @Column(unique = true)
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (TipoUsuario.ADMIN.equals(this.tipoUsuario)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.senha; // Retorna a senha do usuário
    }

    @Override
    public String getUsername() {
        return this.email; // Retorna o email como nome de usuário
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Define se a conta não está expirada
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Define se a conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Define se as credenciais não estão expiradas
    }

    @Override
    public boolean isEnabled() {
        return this.ativo; // Define se a conta está ativa
    }

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

        if (dados.senha() != null) {
            this.senha = dados.senha();
        }

        if (dados.celular() != null) {
            this.celular = dados.celular();
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
