package com.welltrack.service.registrotreino;

import com.welltrack.domain.registrotreino.RegistroTreino;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.registrotreino.DadosAtualizacaoRegistroTreino;
import com.welltrack.dto.registrotreino.DadosCadastroRegistroTreino;
import com.welltrack.dto.registrotreino.DadosListagemRegistroTreino;
import com.welltrack.repository.registrotreino.RegistroTreinoRepository;
import com.welltrack.repository.treino.TreinoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistroTreinoService {

    @Autowired
    private RegistroTreinoRepository repository;

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarUsuario(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public RegistroTreino cadastrar(DadosCadastroRegistroTreino dados, Usuario usuarioLogado) {
        validarUsuario(dados.idUsuario(), usuarioLogado);

        var treino = treinoRepository.getReferenceById(dados.idTreino());
        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var registroTreino = new RegistroTreino(null, dados.dataExecucao(), dados.horaEntrada(),
                dados.horaSaida(), dados.concluido(), treino, usuario);
        return repository.save(registroTreino);
    }

    public Page<DadosListagemRegistroTreino> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarUsuario(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemRegistroTreino::new);
    }

    public RegistroTreino detalhar(UUID idRegistro, Usuario usuarioLogado) {
        var registroTreino = repository.getReferenceById(idRegistro);
        validarUsuario(registroTreino.getUsuario().getIdUsuario(), usuarioLogado);
        return registroTreino;
    }

    public RegistroTreino atualizar(DadosAtualizacaoRegistroTreino dados, Usuario usuarioLogado) {
        var registroTreino = repository.getReferenceById(dados.idRegistro());
        validarUsuario(registroTreino.getUsuario().getIdUsuario(), usuarioLogado);
        registroTreino.atualizar(dados);
        return registroTreino;
    }

    public void deletar(UUID idRegistro, Usuario usuarioLogado) {
        var registroTreino = repository.getReferenceById(idRegistro);
        validarUsuario(registroTreino.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(registroTreino);
    }
}
