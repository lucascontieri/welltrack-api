package com.welltrack.service.usuario;

import com.welltrack.domain.usuario.TipoUsuario;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import com.welltrack.dto.usuario.DadosListagemUsuario;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void validarAutorizacao(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario()) && !TipoUsuario.ADMIN.equals(usuarioLogado.getTipoUsuario())) {
            throw new AccessDeniedException("Acesso negado: Você não tem permissão para acessar ou modificar os dados deste usuário.");
        }
    }

    public Usuario cadastrar(DadosCadastroUsuario dados) {
        // Cadastro é público (qualquer pessoa cria sua própria conta sem estar logado)
        var usuario = new Usuario(dados);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        return repository.save(usuario);
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
