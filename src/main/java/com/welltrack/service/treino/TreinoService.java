package com.welltrack.service.treino;

import com.welltrack.domain.treino.Treino;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.treino.DadosAtualizacaoTreino;
import com.welltrack.dto.treino.DadosCadastroTreino;
import com.welltrack.dto.treino.DadosListagemTreino;
import com.welltrack.repository.treino.TreinoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TreinoService {

    @Autowired
    private TreinoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarUsuario(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Treino cadastrar(DadosCadastroTreino dados, Usuario usuarioLogado) {
        validarUsuario(dados.idUsuario(), usuarioLogado);

        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var treino = new Treino(null, dados.nomeTreino(), dados.dataTreino(), usuario, null, null);
        return repository.save(treino);
    }

    public Page<DadosListagemTreino> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarUsuario(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemTreino::new);
    }

    public Treino detalhar(UUID idTreino, Usuario usuarioLogado) {
        var treino = repository.getReferenceById(idTreino);
        validarUsuario(treino.getUsuario().getIdUsuario(), usuarioLogado);
        return treino;
    }

    public Treino atualizar(DadosAtualizacaoTreino dados, Usuario usuarioLogado) {
        var treino = repository.getReferenceById(dados.idTreino());
        validarUsuario(treino.getUsuario().getIdUsuario(), usuarioLogado);
        treino.atualizar(dados);
        return treino;
    }

    public void deletar(UUID idTreino, Usuario usuarioLogado) {
        var treino = repository.getReferenceById(idTreino);
        validarUsuario(treino.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(treino);
    }
}
