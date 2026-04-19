package com.welltrack.service.usuario;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosAutenticacao;
import com.welltrack.exception.ConflitoException;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.security.DadosTokenJWT;
import com.welltrack.security.TokenService;
import com.welltrack.util.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = repository.findByEmail(EmailUtils.normalizar(username));
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario nao encontrado.");
        }
        return usuario;
    }

    public DadosTokenJWT efetuarLogin(DadosAutenticacao dados) {
        String email = EmailUtils.normalizar(dados.email());
        var usuarioExistente = repository.findOptionalByEmail(email);

        if (usuarioExistente.isPresent()) {
            var usuario = usuarioExistente.get();
            if (usuario.isSomenteGoogle()) {
                throw new ConflitoException("Esta conta esta vinculada ao Google. Use o login com Google.");
            }
        }

        var authenticationToken = new UsernamePasswordAuthenticationToken(email, dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return new DadosTokenJWT(tokenJWT);
    }

    public String gerarToken(Usuario usuario) {
        return tokenService.gerarToken(usuario);
    }
}
