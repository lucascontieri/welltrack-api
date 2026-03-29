package com.welltrack.service.tarefabloco;

import com.welltrack.domain.tarefabloco.TarefaBloco;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tarefabloco.DadosAtualizacaoTarefaBloco;
import com.welltrack.dto.tarefabloco.DadosCadastroTarefaBloco;
import com.welltrack.dto.tarefabloco.DadosListagemTarefaBloco;
import com.welltrack.repository.tarefa.TarefaRepository;
import com.welltrack.repository.tarefabloco.TarefaBlocoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TarefaBlocoService {

    @Autowired
    private TarefaBlocoRepository repository;

    @Autowired
    private TarefaRepository tarefaRepository;

    private void validarIdor(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public TarefaBloco cadastrar(DadosCadastroTarefaBloco dados, Usuario usuarioLogado) {
        var tarefa = tarefaRepository.getReferenceById(dados.idTarefa());
        validarIdor(tarefa.getLista().getUsuario().getIdUsuario(), usuarioLogado);

        TarefaBloco pai = null;
        if (dados.idPai() != null) {
            pai = repository.getReferenceById(dados.idPai());
        }

        var tarefaBloco = new TarefaBloco(null, dados.tipo(), dados.conteudo(), dados.ordem(),
                dados.propriedades(), pai, tarefa);
        return repository.save(tarefaBloco);
    }

    public Page<DadosListagemTarefaBloco> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarIdor(idUsuario, usuarioLogado);
        return repository.findAllByTarefaListaUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemTarefaBloco::new);
    }

    public TarefaBloco detalhar(UUID idBloco, Usuario usuarioLogado) {
        var tarefaBloco = repository.getReferenceById(idBloco);
        validarIdor(tarefaBloco.getTarefa().getLista().getUsuario().getIdUsuario(), usuarioLogado);
        return tarefaBloco;
    }

    public TarefaBloco atualizar(DadosAtualizacaoTarefaBloco dados, Usuario usuarioLogado) {
        var tarefaBloco = repository.getReferenceById(dados.idBloco());
        validarIdor(tarefaBloco.getTarefa().getLista().getUsuario().getIdUsuario(), usuarioLogado);
        tarefaBloco.atualizar(dados);
        return tarefaBloco;
    }

    public void deletar(UUID idBloco, Usuario usuarioLogado) {
        var tarefaBloco = repository.getReferenceById(idBloco);
        validarIdor(tarefaBloco.getTarefa().getLista().getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(tarefaBloco);
    }
}
