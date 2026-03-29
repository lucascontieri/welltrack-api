package com.welltrack.service.refeicao;

import com.welltrack.domain.refeicao.Refeicao;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.refeicao.DadosAtualizacaoRefeicao;
import com.welltrack.dto.refeicao.DadosCadastroRefeicao;
import com.welltrack.dto.refeicao.DadosListagemRefeicao;
import com.welltrack.repository.refeicao.RefeicaoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefeicaoService {

    @Autowired
    private RefeicaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarIdor(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Refeicao cadastrar(DadosCadastroRefeicao dados, Usuario usuarioLogado) {
        validarIdor(dados.idUsuario(), usuarioLogado);

        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var refeicao = new Refeicao(null, dados.nomeRefeicao(), dados.horario(), dados.tipoRecorrencia(),
                dados.diasPersonalizados(), dados.imagemRefeicao(), usuario, null);
        return repository.save(refeicao);
    }

    public Page<DadosListagemRefeicao> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarIdor(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemRefeicao::new);
    }

    public Refeicao detalhar(UUID idRefeicao, Usuario usuarioLogado) {
        var refeicao = repository.getReferenceById(idRefeicao);
        validarIdor(refeicao.getUsuario().getIdUsuario(), usuarioLogado);
        return refeicao;
    }

    public Refeicao atualizar(DadosAtualizacaoRefeicao dados, Usuario usuarioLogado) {
        var refeicao = repository.getReferenceById(dados.idRefeicao());
        validarIdor(refeicao.getUsuario().getIdUsuario(), usuarioLogado);
        refeicao.atualizar(dados);
        return refeicao;
    }

    public void deletar(UUID idRefeicao, Usuario usuarioLogado) {
        var refeicao = repository.getReferenceById(idRefeicao);
        validarIdor(refeicao.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(refeicao);
    }
}
