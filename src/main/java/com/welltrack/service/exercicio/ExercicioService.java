package com.welltrack.service.exercicio;

import com.welltrack.domain.exercicio.Exercicio;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.exercicio.DadosAtualizacaoExercicio;
import com.welltrack.dto.exercicio.DadosCadastroExercicio;
import com.welltrack.dto.exercicio.DadosListagemExercicio;
import com.welltrack.repository.exercicio.ExercicioRepository;
import com.welltrack.repository.grupomuscular.GrupoMuscularRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExercicioService {

    @Autowired
    private ExercicioRepository repository;

    @Autowired
    private GrupoMuscularRepository grupoMuscularRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarIdor(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Exercicio cadastrar(DadosCadastroExercicio dados, Usuario usuarioLogado) {
        validarIdor(dados.idUsuario(), usuarioLogado);

        var grupoMuscular = grupoMuscularRepository.getReferenceById(dados.idGrupo());
        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var exercicio = new Exercicio(null, dados.nomeExercicio(), dados.imagemExercicio(),
                dados.videoExercicio(), grupoMuscular, usuario, null);
        return repository.save(exercicio);
    }

    public Page<DadosListagemExercicio> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarIdor(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemExercicio::new);
    }

    public Exercicio detalhar(UUID idExercicio, Usuario usuarioLogado) {
        var exercicio = repository.getReferenceById(idExercicio);
        validarIdor(exercicio.getUsuario().getIdUsuario(), usuarioLogado);
        return exercicio;
    }

    public Exercicio atualizar(DadosAtualizacaoExercicio dados, Usuario usuarioLogado) {
        var exercicio = repository.getReferenceById(dados.idExercicio());
        validarIdor(exercicio.getUsuario().getIdUsuario(), usuarioLogado);
        exercicio.atualizar(dados);
        return exercicio;
    }

    public void deletar(UUID idExercicio, Usuario usuarioLogado) {
        var exercicio = repository.getReferenceById(idExercicio);
        validarIdor(exercicio.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(exercicio);
    }
}
