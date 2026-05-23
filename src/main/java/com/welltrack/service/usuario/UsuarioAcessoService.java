package com.welltrack.service.usuario;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.repository.tokenrecuperacao.TokenRecuperacaoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.service.email.EmailService;
import com.welltrack.util.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class UsuarioAcessoService {

    private static final int EMAIL_VERIFICACAO_VALIDADE_HORAS = 48;
    private static final int RECUPERACAO_VALIDADE_MINUTOS = 60;
    private static final String ESTILO_BOTAO = "display:inline-block;padding:14px 32px;background-color:#0f766e;color:#ffffff;text-decoration:none;border-radius:8px;font-weight:600;font-size:15px;box-shadow:0 4px 6px -1px rgba(15,118,110,0.15)";

    @Autowired
    private TokenRecuperacaoRepository tokenRecuperacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.public.base-url:http://localhost:8080}")
    private String publicBaseUrl;

    private static String novoTokenSeguro() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    @Transactional
    public void iniciarVerificacaoEmail(Usuario usuario) {
        tokenRecuperacaoRepository.deleteByUsuarioAndTipoAndUsadoFalse(usuario, TipoTokenRecuperacao.VERIFICACAO_EMAIL);
        String token = novoTokenSeguro();
        LocalDateTime agora = LocalDateTime.now();
        var registro = new TokenRecuperacao(
                null,
                token,
                agora.plusHours(EMAIL_VERIFICACAO_VALIDADE_HORAS),
                false,
                0,
                agora,
                TipoTokenRecuperacao.VERIFICACAO_EMAIL,
                usuario);
        tokenRecuperacaoRepository.save(registro);

        String link = publicBaseUrl.replaceAll("/$", "") + "/usuario/confirmar-email?token=" + token;
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:20px;font-weight:700;line-height:1.3">Confirme seu endereço de e-mail</h2>
                <p>Olá, <strong>%s</strong>.</p>
                <p>Obrigado por se cadastrar no <strong>WellTrack</strong>! Para concluir a ativação de sua conta e começar a acompanhar sua saúde, clique no botão abaixo para confirmar seu e-mail:</p>
                <div style="text-align:center;margin:32px 0">
                  <a href="%s" style="%s">Confirmar E-mail</a>
                </div>
                <p style="font-size:14px;color:#64748b;margin:0">Este link de ativação é válido por <strong>%d horas</strong>.</p>
                <p style="font-size:14px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se você não criou uma conta no WellTrack, por favor desconsidere este e-mail.</p>
                """.formatted(nomeSeguro, link, ESTILO_BOTAO, EMAIL_VERIFICACAO_VALIDADE_HORAS);

        emailService.enviarHtml(usuario.getEmail(), "Confirme seu e-mail - WellTrack", html);
    }

    @Transactional
    public void confirmarEmail(String token) {
        var registro = tokenRecuperacaoRepository
                .findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
                        token, LocalDateTime.now(), TipoTokenRecuperacao.VERIFICACAO_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou já utilizado."));
        Usuario usuario = registro.getUsuario();
        usuario.setEmailVerificado(true);
        registro.marcarComoUsado();
        usuarioRepository.save(usuario);
        tokenRecuperacaoRepository.save(registro);
    }

    @Transactional
    public void reenviarVerificacaoEmail(String email) {
        Optional<Usuario> opt = usuarioRepository.findOptionalByEmail(EmailUtils.normalizar(email));
        if (opt.isEmpty()) {
            return;
        }

        Usuario usuario = opt.get();
        if (Boolean.TRUE.equals(usuario.getEmailVerificado()) || usuario.getGoogleId() != null) {
            return;
        }

        iniciarVerificacaoEmail(usuario);
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        Optional<Usuario> opt = usuarioRepository.findOptionalByEmail(EmailUtils.normalizar(email));
        if (opt.isEmpty()) {
            return;
        }

        Usuario usuario = opt.get();
        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || !Boolean.TRUE.equals(usuario.getEmailVerificado())
                || usuario.getSenha() == null) {
            return;
        }

        tokenRecuperacaoRepository.deleteByUsuarioAndTipoAndUsadoFalse(usuario, TipoTokenRecuperacao.RECUPERACAO_SENHA);
        String token = novoTokenSeguro();
        LocalDateTime agora = LocalDateTime.now();
        var registro = new TokenRecuperacao(
                null,
                token,
                agora.plusMinutes(RECUPERACAO_VALIDADE_MINUTOS),
                false,
                0,
                agora,
                TipoTokenRecuperacao.RECUPERACAO_SENHA,
                usuario);
        tokenRecuperacaoRepository.save(registro);

        String link = publicBaseUrl.replaceAll("/$", "") + "/redefinir-senha?token=" + token;
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:20px;font-weight:700;line-height:1.3">Recuperação de Senha</h2>
                <p>Olá, <strong>%s</strong>.</p>
                <p>Recebemos uma solicitação para redefinir a senha da sua conta no <strong>WellTrack</strong>. Clique no botão abaixo para escolher uma nova senha:</p>
                <div style="text-align:center;margin:32px 0">
                  <a href="%s" style="%s">Redefinir Senha</a>
                </div>
                <p style="font-size:14px;color:#64748b;margin:0">Por motivos de segurança, este link é válido por apenas <strong>%d minutos</strong>.</p>
                <p style="font-size:14px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se você não solicitou essa redefinição, nenhuma ação adicional é necessária e você pode desconsiderar este e-mail com segurança.</p>
                """.formatted(nomeSeguro, link, ESTILO_BOTAO, RECUPERACAO_VALIDADE_MINUTOS);

        emailService.enviarHtml(usuario.getEmail(), "Redefinição de senha - WellTrack", html);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        var registro = tokenRecuperacaoRepository
                .findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
                        token, LocalDateTime.now(), TipoTokenRecuperacao.RECUPERACAO_SENHA)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));
        Usuario usuario = registro.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        registro.marcarComoUsado();
        usuarioRepository.save(usuario);
        tokenRecuperacaoRepository.save(registro);

        enviarConfirmacaoSenhaAlterada(usuario);
    }

    private void enviarConfirmacaoSenhaAlterada(Usuario usuario) {
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:20px;font-weight:700;line-height:1.3">Senha alterada com sucesso!</h2>
                <p>Olá, <strong>%s</strong>.</p>
                <p>Este e-mail é para confirmar que a senha da sua conta no <strong>WellTrack</strong> foi alterada recentemente.</p>
                <div style="margin:28px 0;padding:20px;background-color:#fff1f2;border-radius:12px;border-left:4px solid #f43f5e">
                  <p style="margin:0;font-size:14px;color:#be123c;line-height:1.6">
                    <strong>Atenção:</strong> Caso <strong>não</strong> tenha sido você quem realizou essa alteração, entre em contato imediatamente com o nosso suporte.
                  </p>
                </div>
                <p>Se foi você, pode ignorar este aviso e continuar usando sua conta normalmente.</p>
                """
                .formatted(nomeSeguro);

        emailService.enviarHtml(usuario.getEmail(), "Sua senha foi alterada - WellTrack", html);
    }
}
