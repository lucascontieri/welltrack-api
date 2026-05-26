package com.welltrack.controller;

import com.welltrack.service.usuario.UsuarioAcessoService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.welltrack.dto.resposta.DadosMensagemResposta;
import com.welltrack.dto.usuario.DadosReenviarVerificacao;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

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
}
