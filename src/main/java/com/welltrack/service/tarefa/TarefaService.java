package com.welltrack.service.tarefa;

import com.welltrack.domain.tarefa.Tarefa;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tarefa.DadosAtualizacaoTarefa;
import com.welltrack.dto.tarefa.DadosCadastroTarefa;
import com.welltrack.dto.tarefa.DadosListagemTarefa;
import com.welltrack.repository.lista.ListaRepository;
import com.welltrack.repository.tarefa.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository repository;

    @Autowired
    private ListaRepository listaRepository;

    private void validarUsuario(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Tarefa cadastrar(DadosCadastroTarefa dados, Usuario usuarioLogado) {
        var lista = listaRepository.getReferenceById(dados.idLista());
        validarUsuario(lista.getUsuario().getIdUsuario(), usuarioLogado);

        var tarefa = new Tarefa(null, dados.descricao(), dados.status(), dados.prazoMaximo(),
                dados.imagemTarefa(), lista, null);
        return repository.save(tarefa);
    }

    public Page<DadosListagemTarefa> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarUsuario(idUsuario, usuarioLogado);
        return repository.findAllByListaUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemTarefa::new);
    }

    public Tarefa detalhar(UUID idTarefa, Usuario usuarioLogado) {
        var tarefa = repository.getReferenceById(idTarefa);
        validarUsuario(tarefa.getLista().getUsuario().getIdUsuario(), usuarioLogado);
        return tarefa;
    }

    public Tarefa atualizar(DadosAtualizacaoTarefa dados, Usuario usuarioLogado) {
        var tarefa = repository.getReferenceById(dados.idTarefa());
        validarUsuario(tarefa.getLista().getUsuario().getIdUsuario(), usuarioLogado);
        tarefa.atualizar(dados);
        return tarefa;
    }

    public void deletar(UUID idTarefa, Usuario usuarioLogado) {
        var tarefa = repository.getReferenceById(idTarefa);
        validarUsuario(tarefa.getLista().getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(tarefa);
    }
}
