package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosAutenticacao;
import com.welltrack.dto.usuario.DadosGoogleAuth;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.security.DadosTokenJWT;
import com.welltrack.security.TokenService;
import com.welltrack.service.usuario.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        // Verifica se o email pertence a uma conta vinculada ao Google (sem senha)
        var usuarioExistente = usuarioRepository.findOptionalByEmail(dados.email());
        if (usuarioExistente.isPresent()) {
            var usuario = usuarioExistente.get();
            if (usuario.getGoogleId() != null && usuario.getSenha() == null) {
                return ResponseEntity.status(409).body(
                        new DadosErroLogin("Esta conta está vinculada ao Google. Use o login com Google.")
                );
            }
        }

        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

    @PostMapping("/google")
    @Transactional
    public ResponseEntity efetuarLoginGoogle(@RequestBody @Valid DadosGoogleAuth dados) {
        var usuario = googleAuthService.autenticarComGoogle(dados.token());
        var tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

    private record DadosErroLogin(String mensagem) {}
}
