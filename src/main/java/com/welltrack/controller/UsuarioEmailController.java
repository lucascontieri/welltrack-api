package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.resposta.DadosMensagemResposta;
import com.welltrack.dto.usuario.DadosCodigoVerificacao;
import com.welltrack.dto.usuario.DadosRedefinirSenha;
import com.welltrack.dto.usuario.DadosReenviarVerificacao;
import com.welltrack.dto.usuario.DadosTokenRedefinicao;
import com.welltrack.service.usuario.UsuarioAcessoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("usuario")
public class UsuarioEmailController {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    @GetMapping(value = "/confirmar-email", produces = MediaType.TEXT_HTML_VALUE)
    @Transactional
    public ResponseEntity<String> confirmarEmail(@RequestParam @NotBlank String token) {
        try {
            var usuario = usuarioAcessoService.confirmarEmail(token);
            String html = usuarioAcessoService.montarPaginaHtmlConfirmacaoSucesso(usuario);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        } catch (IllegalArgumentException e) {
            String html = usuarioAcessoService.montarPaginaHtmlConfirmacaoErro(
                    "O link de confirmação é inválido ou já foi utilizado. Solicite um novo link pelo aplicativo.");
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        }
    }

    @PostMapping("/reenviar-verificacao")
    @Transactional
    public ResponseEntity<DadosMensagemResposta> reenviarVerificacao(
            @RequestBody @Valid DadosReenviarVerificacao dados) {
        usuarioAcessoService.reenviarVerificacaoEmail(dados.email());
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Se existir uma conta pendente de verificacao para este e-mail, enviaremos um novo link."));
    }

    // ─── Definição de senha (usuários Google-only, autenticados) ──────

    @PostMapping("/solicitar-definicao-senha")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<DadosMensagemResposta> solicitarDefinicaoSenha(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioAcessoService.solicitarDefinicaoSenha(usuarioLogado);
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Um código de verificação foi enviado para o seu e-mail."));
    }

    @PostMapping("/validar-codigo-definicao-senha")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<DadosTokenRedefinicao> validarCodigoDefinicaoSenha(
            @RequestBody @Valid DadosCodigoVerificacao dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        String tokenDefinicao = usuarioAcessoService.validarCodigoDefinicaoSenha(usuarioLogado, dados.codigo());
        return ResponseEntity.ok(new DadosTokenRedefinicao(tokenDefinicao));
    }

    @PostMapping("/definir-senha")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<DadosMensagemResposta> definirSenha(
            @RequestBody @Valid DadosRedefinirSenha dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioAcessoService.definirSenha(dados.token(), dados.novaSenha(), usuarioLogado);
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Sua senha foi definida com sucesso. Agora voce pode fazer login com e-mail e senha."));
    }
}
