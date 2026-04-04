package com.welltrack.service.usuario;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    @Autowired
    private UsuarioRepository repository;

    @Value("${api.google.client-id}")
    private String googleClientId;

    public Usuario autenticarComGoogle(String idTokenString) {
        GoogleIdToken.Payload payload = verificarToken(idTokenString);

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String nome = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        // 1. Buscar por googleId (já logou com Google antes)
        var usuarioExistente = repository.findByGoogleId(googleId);
        if (usuarioExistente.isPresent()) {
            return usuarioExistente.get();
        }

        // 2. Buscar por email (usuário já cadastrado com email/senha, agora quer vincular Google)
        var usuarioPorEmail = repository.findOptionalByEmail(email);
        if (usuarioPorEmail.isPresent()) {
            var usuario = usuarioPorEmail.get();
            usuario.setGoogleId(googleId);
            return repository.save(usuario);
        }

        // 3. Novo usuário — criar conta apenas com dados do Google
        var novoUsuario = new Usuario(nome, email, googleId, picture);
        return repository.save(novoUsuario);
    }

    private GoogleIdToken.Payload verificarToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Token do Google inválido ou expirado.");
            }

            return idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao verificar o token do Google.", e);
        }
    }
}
