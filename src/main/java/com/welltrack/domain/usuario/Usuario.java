package com.welltrack.domain.usuario;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.exercicio.Exercicio;
import com.welltrack.domain.lista.Lista;
import com.welltrack.domain.refeicao.Refeicao;
import com.welltrack.domain.registrotreino.RegistroTreino;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.treino.Treino;
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

// Mapeando a entidade JPA 'Usuario' no banco de dados PostgreSQL na tabela 'Usuario'
@Entity
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

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(unique = true, length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario = TipoUsuario.USER;

    private LocalDate dataNascimento;

    @Column(precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(precision = 3, scale = 2)
    private BigDecimal altura;

    @Column(nullable = false, unique = true)
    private String email;

    private String senha;

    @Column(length = 20)
    private String celular;

    @Column(length = 150)
    private String logradouro;

    private Integer numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 8)
    private String cep;

    @Column(length = 500)
    private String imagemUsuario;

    @Column(unique = true)
    private String googleId;

    /**
     * Contas criadas com e-mail/senha ficam false até o usuário confirmar o link
     * enviado por e-mail.
     */
    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    private Boolean ativo;

    // Mapeando os relacionamentos

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<TokenRecuperacao> tokens;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Exercicio> exercicios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Treino> treinos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<RegistroTreino> registrosTreino;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Alimento> alimentos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Refeicao> refeicoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Lista> listas;

    // ----------------------------------------------------------------

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
        return this.senha != null ? this.senha : ""; // Retorna a senha do usuário (vazia se login via Google)
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
        if (!Boolean.TRUE.equals(this.ativo)) {
            return false;
        }
        if (this.googleId != null) {
            return true;
        }
        return Boolean.TRUE.equals(this.emailVerificado);
    }

    public Usuario(DadosCadastroUsuario dados) {
        this.ativo = true;
        this.emailVerificado = false;
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

    // Construtor para criação de usuário via Google OAuth 2.0
    public Usuario(String nome, String email, String googleId, String imagemUsuario) {
        this.ativo = true;
        this.emailVerificado = true;
        this.nome = nome;
        this.email = email;
        this.googleId = googleId;
        this.imagemUsuario = imagemUsuario;
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

    public void ativar() {
        this.ativo = true;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSenhaCadastrada() {
        return this.senha != null;
    }

    public boolean isSomenteGoogle() {
        return this.googleId != null && this.senha == null;
    }
}
