package com.welltrack.controller;

import com.welltrack.dto.resposta.DadosMensagemResposta;
import com.welltrack.dto.usuario.DadosRedefinirSenha;
import com.welltrack.dto.usuario.DadosSolicitarRecuperacaoSenha;
import com.welltrack.dto.usuario.DadosAutenticacao;
import com.welltrack.dto.usuario.DadosGoogleAuth;
import com.welltrack.security.DadosTokenJWT;
import com.welltrack.security.TokenService;
import com.welltrack.service.usuario.UsuarioAcessoService;
import com.welltrack.service.usuario.AutenticacaoService;
import com.welltrack.service.usuario.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var token = autenticacaoService.efetuarLogin(dados);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/google")
    @Transactional
    public ResponseEntity<DadosTokenJWT> efetuarLoginGoogle(@RequestBody @Valid DadosGoogleAuth dados) {
        var usuario = googleAuthService.autenticarComGoogle(dados.token());
        var tokenJWT = autenticacaoService.gerarToken(usuario);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

    @PostMapping("/solicitar-recuperacao-senha")
    @Transactional
    public ResponseEntity<DadosMensagemResposta> solicitarRecuperacaoSenha(@RequestBody @Valid DadosSolicitarRecuperacaoSenha dados) {
        usuarioAcessoService.solicitarRecuperacaoSenha(dados.email());
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Se o e-mail estiver cadastrado, voce recebera instrucoes para redefinir a senha."));
    }

    @PostMapping("/redefinir-senha")
    @Transactional
    public ResponseEntity<DadosMensagemResposta> redefinirSenha(@RequestBody @Valid DadosRedefinirSenha dados) {
        usuarioAcessoService.redefinirSenha(dados.token(), dados.novaSenha());
        return ResponseEntity.ok(new DadosMensagemResposta(
                "Sua senha foi redefinida com sucesso. Um e-mail de confirmacao foi enviado para voce."));
    }
}
