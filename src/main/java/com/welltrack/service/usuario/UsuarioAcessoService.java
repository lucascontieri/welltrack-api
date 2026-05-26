package com.welltrack.service.usuario;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.exception.ValidacaoException;
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
    private static final int MAX_TENTATIVAS_CODIGO = 5;

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

    // ─── Geração de tokens/códigos ─────────────────────────────────────

    private static String novoTokenSeguro() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String novoCodigoSeisDigitos() {
        return String.valueOf(new SecureRandom().nextInt(100000, 1000000));
    }

    // ─── 1. Verificação de E-mail (cadastro email/senha) ──────────────

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
                <h2 style="color:#0f172a;margin-top:0;font-size:22px;font-weight:700;line-height:1.3">
                  Confirme seu endereço de e-mail
                </h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Obrigado por se cadastrar no <strong>WellTrack</strong>! Para concluir a ativação de sua conta e começar a acompanhar sua saúde, clique no botão abaixo:</p>
                <div style="text-align:center;margin:32px 0">
                  <a href="%s" style="display:inline-block;padding:16px 40px;background:linear-gradient(135deg,#0f766e 0%%,#14b8a6 100%%);color:#ffffff;text-decoration:none;border-radius:12px;font-weight:700;font-size:16px;letter-spacing:0.5px;box-shadow:0 4px 14px rgba(15,118,110,0.35)">Confirmar E-mail</a>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#fffbeb;border-radius:10px;border-left:4px solid #f59e0b">
                  <p style="margin:0;font-size:14px;color:#92400e;line-height:1.6">
                    <strong>Atenção:</strong> Este link de ativação é válido por no máximo <strong>%d horas</strong>. Após esse período, será necessário solicitar um novo link de verificação pelo aplicativo.
                  </p>
                </div>
                <p style="font-size:13px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se você não criou uma conta no WellTrack, por favor desconsidere este e-mail.</p>
                """.formatted(nomeSeguro, link, EMAIL_VERIFICACAO_VALIDADE_HORAS);

        emailService.enviarHtml(usuario.getEmail(), "Confirme seu e-mail - WellTrack", html);
    }

    @Transactional
    public Usuario confirmarEmail(String token) {
        var registro = tokenRecuperacaoRepository
                .findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
                        token, LocalDateTime.now(), TipoTokenRecuperacao.VERIFICACAO_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou já utilizado."));
        Usuario usuario = registro.getUsuario();
        usuario.setEmailVerificado(true);
        registro.marcarComoUsado();
        usuarioRepository.save(usuario);
        tokenRecuperacaoRepository.save(registro);
        return usuario;
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

    // ─── 2. Boas-vindas Google (novo usuário) ─────────────────────────

    public void enviarBoasVindasGoogle(Usuario usuario) {
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:22px;font-weight:700;line-height:1.3">
                  Bem-vindo ao WellTrack!
                </h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Sua conta foi criada com sucesso através do <strong>Google</strong>. Agora você pode acessar todos os recursos do WellTrack para acompanhar sua saúde e bem-estar.</p>
                <div style="text-align:center;margin:32px 0;padding:28px;background:linear-gradient(135deg,#f0fdfa 0%%,#ccfbf1 100%%);border-radius:16px;border:1px solid #99f6e4">
                  <p style="margin:0;font-size:18px;font-weight:700;color:#0f766e">Conta criada com sucesso!</p>
                  <p style="margin:8px 0 0 0;font-size:14px;color:#115e59">Acesse o aplicativo WellTrack e comece agora.</p>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#f8fafc;border-radius:10px;border-left:4px solid #0f766e">
                  <p style="margin:0;font-size:14px;color:#334155;line-height:1.6">
                    <strong>Seus dados sincronizados:</strong><br>
                    E-mail: %s<br>
                    Vinculado ao Google
                  </p>
                </div>
                <p style="font-size:13px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se você não realizou este cadastro, por favor desconsidere este e-mail.</p>
                """.formatted(nomeSeguro, HtmlUtils.htmlEscape(usuario.getEmail()));

        emailService.enviarHtml(usuario.getEmail(), "Bem-vindo ao WellTrack!", html);
    }

    // ─── 3. Notificação de vinculação de conta ────────────────────────

    public void enviarNotificacaoVinculacao(Usuario usuario) {
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:22px;font-weight:700;line-height:1.3">
                  Conta Google vinculada
                </h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Informamos que sua conta do <strong>Google</strong> foi vinculada com sucesso à sua conta existente no <strong>WellTrack</strong>.</p>
                <div style="text-align:center;margin:32px 0;padding:28px;background:linear-gradient(135deg,#f0fdfa 0%%,#ccfbf1 100%%);border-radius:16px;border:1px solid #99f6e4">
                  <p style="margin:0;font-size:18px;font-weight:700;color:#0f766e">Contas sincronizadas!</p>
                  <p style="margin:8px 0 0 0;font-size:14px;color:#115e59">Agora você pode fazer login com e-mail/senha ou pelo Google.</p>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#f8fafc;border-radius:10px;border-left:4px solid #0f766e">
                  <p style="margin:0;font-size:14px;color:#334155;line-height:1.6">
                    <strong>Informações da conta:</strong><br>
                    E-mail: %s<br>
                    Login por e-mail/senha: ativado<br>
                    Login por Google: ativado
                  </p>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#fff1f2;border-radius:10px;border-left:4px solid #f43f5e">
                  <p style="margin:0;font-size:14px;color:#be123c;line-height:1.6">
                    <strong>Atenção:</strong> Se você <strong>não</strong> realizou essa vinculação, entre em contato imediatamente com o nosso suporte.
                  </p>
                </div>
                <p style="font-size:13px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se foi você, pode ignorar este aviso e continuar usando sua conta normalmente.</p>
                """.formatted(nomeSeguro, HtmlUtils.htmlEscape(usuario.getEmail()));

        emailService.enviarHtml(usuario.getEmail(), "Conta Google vinculada - WellTrack", html);
    }

    // ─── 4. Recuperação de Senha (código de 6 dígitos) ────────────────

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
        String codigo = novoCodigoSeisDigitos();
        LocalDateTime agora = LocalDateTime.now();
        var registro = new TokenRecuperacao(
                null,
                codigo,
                agora.plusMinutes(RECUPERACAO_VALIDADE_MINUTOS),
                false,
                0,
                agora,
                TipoTokenRecuperacao.RECUPERACAO_SENHA,
                usuario);
        tokenRecuperacaoRepository.save(registro);

        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:22px;font-weight:700;line-height:1.3">
                  Recuperação de Senha
                </h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Recebemos uma solicitação para redefinir a senha da sua conta no <strong>WellTrack</strong>. Use o código abaixo no aplicativo para continuar:</p>
                <div style="text-align:center;margin:32px 0;padding:32px;background:linear-gradient(135deg,#f8fafc 0%%,#f1f5f9 100%%);border-radius:16px;border:2px dashed #cbd5e1">
                  <p style="margin:0 0 8px 0;font-size:13px;font-weight:600;color:#64748b;text-transform:uppercase;letter-spacing:2px">Seu código de verificação</p>
                  <p style="margin:0;font-size:40px;font-weight:800;letter-spacing:12px;color:#0f766e;font-family:'Courier New',Courier,monospace">%s</p>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#fffbeb;border-radius:10px;border-left:4px solid #f59e0b">
                  <p style="margin:0;font-size:14px;color:#92400e;line-height:1.6">
                    <strong>Atenção:</strong> Por motivos de segurança, este código é válido por no máximo <strong>%d minutos</strong> e pode ser usado uma única vez. Após esse período, será necessário solicitar um novo código.
                  </p>
                </div>
                <p style="font-size:13px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se você não solicitou essa redefinição, nenhuma ação adicional é necessária e você pode desconsiderar este e-mail com segurança.</p>
                """.formatted(nomeSeguro, codigo, RECUPERACAO_VALIDADE_MINUTOS);

        emailService.enviarHtml(usuario.getEmail(), "Código de recuperação de senha - WellTrack", html);
    }

    @Transactional
    public String validarCodigoRecuperacao(String email, String codigo) {
        String emailNormalizado = EmailUtils.normalizar(email);
        Optional<Usuario> opt = usuarioRepository.findOptionalByEmail(emailNormalizado);
        if (opt.isEmpty()) {
            throw new ValidacaoException("Código inválido ou expirado.");
        }

        Usuario usuario = opt.get();

        // Busca o token de recuperação ativo pelo USUÁRIO (não pelo código)
        var registroOpt = tokenRecuperacaoRepository
                .findByUsuarioAndTipoAndUsadoFalseAndExpiracaoAfter(
                        usuario, TipoTokenRecuperacao.RECUPERACAO_SENHA, LocalDateTime.now());

        if (registroOpt.isEmpty()) {
            throw new ValidacaoException("Código inválido ou expirado.");
        }

        var registro = registroOpt.get();

        // Controle de tentativas — verifica ANTES de comparar o código
        if (registro.getTentativas() >= MAX_TENTATIVAS_CODIGO) {
            registro.marcarComoUsado();
            tokenRecuperacaoRepository.save(registro);
            throw new ValidacaoException("Número máximo de tentativas excedido. Solicite um novo código.");
        }

        // Compara o código informado com o armazenado
        if (!registro.getToken().equals(codigo)) {
            registro.incrementarTentativas();
            tokenRecuperacaoRepository.save(registro);

            int restantes = MAX_TENTATIVAS_CODIGO - registro.getTentativas();
            if (restantes <= 0) {
                registro.marcarComoUsado();
                tokenRecuperacaoRepository.save(registro);
                throw new ValidacaoException("Número máximo de tentativas excedido. Solicite um novo código.");
            }

            throw new ValidacaoException("Código incorreto. Você ainda tem " + restantes + " tentativa(s).");
        }

        // Código válido — marcar como usado e gerar token seguro para redefinição
        registro.marcarComoUsado();
        tokenRecuperacaoRepository.save(registro);

        // Gerar um novo token seguro para a etapa de redefinição
        String tokenRedefinicao = novoTokenSeguro();
        LocalDateTime agora = LocalDateTime.now();
        var registroRedefinicao = new TokenRecuperacao(
                null,
                tokenRedefinicao,
                agora.plusMinutes(15),
                false,
                0,
                agora,
                TipoTokenRecuperacao.RECUPERACAO_SENHA,
                usuario);
        tokenRecuperacaoRepository.save(registroRedefinicao);

        return tokenRedefinicao;
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        var registro = tokenRecuperacaoRepository
                .findByTokenAndUsadoFalseAndExpiracaoAfterAndTipo(
                        token, LocalDateTime.now(), TipoTokenRecuperacao.RECUPERACAO_SENHA)
                .orElseThrow(() -> new ValidacaoException("Token inválido ou expirado."));
        Usuario usuario = registro.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        registro.marcarComoUsado();
        usuarioRepository.save(usuario);
        tokenRecuperacaoRepository.save(registro);

        enviarConfirmacaoSenhaAlterada(usuario);
    }

    // ─── 5. Confirmação de senha alterada ─────────────────────────────

    private void enviarConfirmacaoSenhaAlterada(Usuario usuario) {
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        String html = """
                <h2 style="color:#0f172a;margin-top:0;font-size:22px;font-weight:700;line-height:1.3">
                  Senha alterada com sucesso!
                </h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Este e-mail é para confirmar que a senha da sua conta no <strong>WellTrack</strong> foi alterada recentemente.</p>
                <div style="text-align:center;margin:32px 0;padding:28px;background:linear-gradient(135deg,#f0fdfa 0%%,#ccfbf1 100%%);border-radius:16px;border:1px solid #99f6e4">
                  <p style="margin:0;font-size:18px;font-weight:700;color:#0f766e">Sua senha foi atualizada</p>
                  <p style="margin:8px 0 0 0;font-size:14px;color:#115e59">Você já pode fazer login com a nova senha.</p>
                </div>
                <div style="margin:24px 0;padding:16px 20px;background-color:#fff1f2;border-radius:10px;border-left:4px solid #f43f5e">
                  <p style="margin:0;font-size:14px;color:#be123c;line-height:1.6">
                    <strong>Atenção:</strong> Caso <strong>não</strong> tenha sido você quem realizou essa alteração, entre em contato imediatamente com o nosso suporte.
                  </p>
                </div>
                <p style="font-size:13px;color:#94a3b8;margin:24px 0 0 0;border-top:1px solid #f1f5f9;padding-top:16px">Se foi você, pode ignorar este aviso e continuar usando sua conta normalmente.</p>
                """
                .formatted(nomeSeguro);

        emailService.enviarHtml(usuario.getEmail(), "Sua senha foi alterada - WellTrack", html);
    }

    // ─── 6. Páginas HTML de confirmação ───────────────────────────────

    public String montarPaginaHtmlConfirmacaoSucesso(Usuario usuario) {
        String nomeSeguro = HtmlUtils.htmlEscape(usuario.getNome());
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>E-mail confirmado - WellTrack</title>
                  <link rel="preconnect" href="https://fonts.googleapis.com">
                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
                  <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                      background: linear-gradient(135deg, #0f766e 0%%, #14b8a6 50%%, #5eead4 100%%);
                      min-height: 100vh;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      padding: 24px;
                      -webkit-font-smoothing: antialiased;
                    }
                    .card {
                      background: #ffffff;
                      border-radius: 24px;
                      box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25), 0 0 0 1px rgba(255,255,255,0.1);
                      max-width: 480px;
                      width: 100%%;
                      overflow: hidden;
                      animation: slideUp 0.6s ease-out;
                    }
                    @keyframes slideUp {
                      from { opacity: 0; transform: translateY(30px); }
                      to { opacity: 1; transform: translateY(0); }
                    }
                    .card-header {
                      background: linear-gradient(135deg, #0f766e 0%%, #14b8a6 100%%);
                      padding: 40px 32px 32px;
                      text-align: center;
                    }
                    .logo-container {
                      margin-bottom: 20px;
                    }
                    .logo-container img {
                      max-width: 140px;
                      height: auto;
                    }
                    .card-header h1 {
                      color: #ffffff;
                      font-size: 24px;
                      font-weight: 800;
                      line-height: 1.3;
                    }
                    .card-body {
                      padding: 36px 32px 40px;
                      text-align: center;
                    }
                    .card-body p {
                      color: #475569;
                      font-size: 16px;
                      line-height: 1.7;
                      margin-bottom: 16px;
                    }
                    .card-body .name {
                      color: #0f766e;
                      font-weight: 700;
                    }
                    .success-box {
                      margin: 24px 0;
                      padding: 24px;
                      background: linear-gradient(135deg, #f0fdfa, #ccfbf1);
                      border: 1px solid #99f6e4;
                      border-radius: 16px;
                    }
                    .success-box .title {
                      font-size: 18px;
                      font-weight: 700;
                      color: #0f766e;
                      margin-bottom: 8px;
                    }
                    .success-box .subtitle {
                      font-size: 14px;
                      color: #115e59;
                      margin: 0;
                    }
                    .footer {
                      padding: 20px 32px;
                      background: #f8fafc;
                      border-top: 1px solid #f1f5f9;
                      text-align: center;
                    }
                    .footer p {
                      color: #94a3b8;
                      font-size: 12px;
                      line-height: 1.6;
                    }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="card-header">
                      <h1>Conta ativada com sucesso!</h1>
                    </div>
                    <div class="card-body">
                      <p>Olá, <span class="name">%s</span>!</p>
                      <p>Seu e-mail foi confirmado e sua conta no <strong>WellTrack</strong> está pronta para uso.</p>
                      <div class="success-box">
                        <p class="title">Acesse o aplicativo WellTrack</p>
                        <p class="subtitle">Você já pode fechar esta página e abrir o app.</p>
                      </div>
                    </div>
                    <div class="footer">
                      <p>&copy; 2026 WellTrack. Todos os direitos reservados.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(nomeSeguro);
    }

    public String montarPaginaHtmlConfirmacaoErro(String mensagem) {
        String mensagemSegura = HtmlUtils.htmlEscape(mensagem);
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Erro na confirmação - WellTrack</title>
                  <link rel="preconnect" href="https://fonts.googleapis.com">
                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
                  <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                      background: linear-gradient(135deg, #991b1b 0%%, #dc2626 50%%, #f87171 100%%);
                      min-height: 100vh;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      padding: 24px;
                      -webkit-font-smoothing: antialiased;
                    }
                    .card {
                      background: #ffffff;
                      border-radius: 24px;
                      box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25), 0 0 0 1px rgba(255,255,255,0.1);
                      max-width: 480px;
                      width: 100%%;
                      overflow: hidden;
                      animation: slideUp 0.6s ease-out;
                    }
                    @keyframes slideUp {
                      from { opacity: 0; transform: translateY(30px); }
                      to { opacity: 1; transform: translateY(0); }
                    }
                    .card-header {
                      background: linear-gradient(135deg, #991b1b 0%%, #dc2626 100%%);
                      padding: 40px 32px 32px;
                      text-align: center;
                    }
                    .card-header h1 {
                      color: #ffffff;
                      font-size: 24px;
                      font-weight: 800;
                      line-height: 1.3;
                    }
                    .card-body {
                      padding: 36px 32px 40px;
                      text-align: center;
                    }
                    .card-body p {
                      color: #475569;
                      font-size: 16px;
                      line-height: 1.7;
                      margin-bottom: 16px;
                    }
                    .error-box {
                      margin: 24px 0;
                      padding: 16px 20px;
                      background-color: #fef2f2;
                      border-radius: 12px;
                      border: 1px solid #fecaca;
                    }
                    .error-box p {
                      color: #991b1b;
                      font-size: 14px;
                      margin: 0;
                    }
                    .footer {
                      padding: 20px 32px;
                      background: #f8fafc;
                      border-top: 1px solid #f1f5f9;
                      text-align: center;
                    }
                    .footer p {
                      color: #94a3b8;
                      font-size: 12px;
                      line-height: 1.6;
                    }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="card-header">
                      <h1>Falha na confirmação</h1>
                    </div>
                    <div class="card-body">
                      <p>Não foi possível confirmar seu e-mail.</p>
                      <div class="error-box">
                        <p>%s</p>
                      </div>
                      <p style="font-size:14px;color:#64748b">Solicite um novo link de verificação pelo aplicativo WellTrack.</p>
                    </div>
                    <div class="footer">
                      <p>&copy; 2026 WellTrack. Todos os direitos reservados.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(mensagemSegura);
    }
}
