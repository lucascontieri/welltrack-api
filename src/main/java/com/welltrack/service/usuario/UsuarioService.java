package com.welltrack.service.usuario;

import com.welltrack.domain.usuario.TipoUsuario;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import com.welltrack.dto.usuario.DadosListagemUsuario;
import com.welltrack.exception.ConflitoException;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.util.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    private void validarAutorizacao(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario()) && !TipoUsuario.ADMIN.equals(usuarioLogado.getTipoUsuario())) {
            throw new AccessDeniedException("Acesso negado: Voce nao tem permissao para acessar ou modificar os dados deste usuario.");
        }
    }

    public Usuario cadastrar(DadosCadastroUsuario dados) {
        if (dados.cpf() != null && repository.existsByCpf(dados.cpf())) {
            throw new ConflitoException("Ja existe um usuario cadastrado com este CPF.");
        }

        String emailNormalizado = EmailUtils.normalizar(dados.email());
        var usuarioExistente = repository.findOptionalByEmail(emailNormalizado);
        
        if (usuarioExistente.isPresent()) {
            var usuario = usuarioExistente.get();
            if (usuario.getGoogleId() != null && usuario.getSenha() == null) {
                throw new ConflitoException("Este e-mail esta vinculado ao Google. Por favor, utilize o login social.");
            }
            throw new ConflitoException("Este e-mail ja esta cadastrado. Tente recuperar sua senha ou faca login.");
        }

        var usuario = new Usuario(dados);
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuario = repository.save(usuario);
        usuarioAcessoService.iniciarVerificacaoEmail(usuario);
        return usuario;
    }

    public Page<DadosListagemUsuario> listar(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemUsuario::new);
    }

    public Usuario atualizar(DadosAtualizacaoUsuario dados, Usuario usuarioLogado) {
        validarAutorizacao(dados.idUsuario(), usuarioLogado);
        var usuario = repository.getReferenceById(dados.idUsuario());
        usuario.atualizarInformacoes(dados);
        if (dados.senha() != null) {
            usuario.setSenha(passwordEncoder.encode(dados.senha()));
        }
        return usuario;
    }

    public void deletar(UUID idUsuario, Usuario usuarioLogado) {
        validarAutorizacao(idUsuario, usuarioLogado);
        var usuario = repository.getReferenceById(idUsuario);
        usuario.inativar();
    }

    public Usuario detalhar(UUID idUsuario, Usuario usuarioLogado) {
        validarAutorizacao(idUsuario, usuarioLogado);
        return repository.getReferenceById(idUsuario);
    }
}
