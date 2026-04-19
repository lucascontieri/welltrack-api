package com.welltrack.controller;

import com.welltrack.dto.resposta.DadosMensagemResposta;
import com.welltrack.dto.usuario.DadosReenviarVerificacao;
import com.welltrack.service.usuario.UsuarioAcessoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("usuario")
public class UsuarioPublicoController {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    @GetMapping("/confirmar-email")
    @Transactional
    public ResponseEntity<DadosMensagemResposta> confirmarEmail(@RequestParam @NotBlank String token) {
        usuarioAcessoService.confirmarEmail(token);
        return ResponseEntity.ok(new DadosMensagemResposta("E-mail confirmado. Voce ja pode fazer login."));
    }

    @PostMapping("/reenviar-verificacao")
    @Transactional
    public ResponseEntity<DadosMensagemResposta> reenviarVerificacao(@RequestBody @Valid DadosReenviarVerificacao dados) {
        usuarioAcessoService.reenviarVerificacaoEmail(dados.email());
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Se existir uma conta pendente de verificacao para este e-mail, enviaremos um novo link."));
    }
}
