package com.welltrack.service.usuario;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.repository.tokenrecuperacao.TokenRecuperacaoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAcessoServiceTest {

    @InjectMocks
    private UsuarioAcessoService service;

    @Mock
    private TokenRecuperacaoRepository tokenRecuperacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "publicBaseUrl", "https://app.welltrack.com");
    }

    @Test
    @DisplayName("Deve reenviar verificacao para conta local pendente usando e-mail normalizado")
    void reenviarVerificacaoEmail_CenarioSucesso() {
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findOptionalByEmail("usuario@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getEmailVerificado()).thenReturn(false);
        when(usuario.getGoogleId()).thenReturn(null);
        when(usuario.getNome()).thenReturn("Lucas");
        when(usuario.getEmail()).thenReturn("usuario@email.com");

        service.reenviarVerificacaoEmail("  Usuario@Email.com  ");

        verify(tokenRecuperacaoRepository)
                .deleteByUsuarioAndTipoAndUsadoFalse(usuario, TipoTokenRecuperacao.VERIFICACAO_EMAIL);
        verify(tokenRecuperacaoRepository).save(any(TokenRecuperacao.class));
        verify(emailService).enviarHtml(eq("usuario@email.com"), contains("Confirme"), contains("Confirmar e-mail"));
    }

    @Test
    @DisplayName("Deve solicitar recuperacao de senha apenas para conta local ativa e verificada")
    void solicitarRecuperacaoSenha_CenarioSucesso() {
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findOptionalByEmail("usuario@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getEmailVerificado()).thenReturn(true);
        when(usuario.getSenha()).thenReturn("senha-criptografada");
        when(usuario.getNome()).thenReturn("Lucas");
        when(usuario.getEmail()).thenReturn("usuario@email.com");

        service.solicitarRecuperacaoSenha("Usuario@Email.com");

        verify(tokenRecuperacaoRepository)
                .deleteByUsuarioAndTipoAndUsadoFalse(usuario, TipoTokenRecuperacao.RECUPERACAO_SENHA);
        verify(tokenRecuperacaoRepository).save(any(TokenRecuperacao.class));
        verify(emailService).enviarHtml(eq("usuario@email.com"), contains("Redefinição"), contains("Redefinir senha"));
    }

    @Test
    @DisplayName("Nao deve solicitar recuperacao para conta nao verificada")
    void solicitarRecuperacaoSenha_CenarioContaNaoVerificada() {
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findOptionalByEmail("usuario@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getEmailVerificado()).thenReturn(false);

        service.solicitarRecuperacaoSenha("usuario@email.com");

        verify(tokenRecuperacaoRepository, never()).save(any(TokenRecuperacao.class));
        verify(emailService, never()).enviarHtml(any(), any(), any());
    }

    @Test
    @DisplayName("Deve redefinir senha e invalidar o token usado")
    void redefinirSenha_CenarioSucesso() {
        Usuario usuario = mock(Usuario.class);
        TokenRecuperacao token = new TokenRecuperacao(
                UUID.randomUUID(),
                "token-seguro",
                LocalDateTime.now().plusMinutes(30),
                false,
                0,
                LocalDateTime.now(),
                TipoTokenRecuperacao.RECUPERACAO_SENHA,
                usuario);

        when(tokenRecuperacaoRepository.findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
                eq("token-seguro"), any(LocalDateTime.class), eq(TipoTokenRecuperacao.RECUPERACAO_SENHA)))
                .thenReturn(Optional.of(token));
        when(usuario.getNome()).thenReturn("Lucas");
        when(usuario.getEmail()).thenReturn("usuario@email.com");
        when(passwordEncoder.encode("NovaSenha123")).thenReturn("nova-senha-criptografada");

        service.redefinirSenha("token-seguro", "NovaSenha123");

        verify(usuario).setSenha("nova-senha-criptografada");
        verify(usuarioRepository).save(usuario);
        verify(tokenRecuperacaoRepository).save(token);
        assertTrue(token.getUsado());
    }
}
