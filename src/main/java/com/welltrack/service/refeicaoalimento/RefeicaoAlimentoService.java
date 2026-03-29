package com.welltrack.service.refeicaoalimento;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.refeicao.Refeicao;
import com.welltrack.domain.refeicaoalimento.RefeicaoAlimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.refeicaoalimento.DadosAtualizacaoRefeicaoAlimento;
import com.welltrack.dto.refeicaoalimento.DadosCadastroRefeicaoAlimento;
import com.welltrack.dto.refeicaoalimento.DadosDetalhamentoRefeicaoAlimento;
import com.welltrack.repository.alimento.AlimentoRepository;
import com.welltrack.repository.refeicao.RefeicaoRepository;
import com.welltrack.repository.refeicaoalimento.RefeicaoAlimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefeicaoAlimentoService {

    @Autowired
    private RefeicaoAlimentoRepository repository;

    @Autowired
    private RefeicaoRepository refeicaoRepository;

    @Autowired
    private AlimentoRepository alimentoRepository;

    private void validarIdor(Refeicao refeicao, Alimento alimento, Usuario usuarioLogado) {
        if (!refeicao.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario()) ||
                !alimento.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: A refeição ou o alimento não pertencem ao usuário logado.");
        }
    }

    public DadosDetalhamentoRefeicaoAlimento cadastrar(DadosCadastroRefeicaoAlimento dados, Usuario usuarioLogado) {
        var refeicao = refeicaoRepository.getReferenceById(dados.idRefeicao());
        var alimento = alimentoRepository.getReferenceById(dados.idAlimento());

        validarIdor(refeicao, alimento, usuarioLogado);

        var id = new RefeicaoAlimento.idRefeicaoAlimento(dados.idRefeicao(), dados.idAlimento());
        var refeicaoAlimento = new RefeicaoAlimento(id, dados.quantidade(), dados.unidadeMedida(), refeicao, alimento);

        repository.save(refeicaoAlimento);
        return new DadosDetalhamentoRefeicaoAlimento(refeicaoAlimento);
    }

    public Page<DadosDetalhamentoRefeicaoAlimento> listarPorUsuario(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        if (!idUsuario.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
        return repository.findAllByRefeicaoUsuarioIdUsuario(idUsuario, paginacao)
                .map(DadosDetalhamentoRefeicaoAlimento::new);
    }

    public DadosDetalhamentoRefeicaoAlimento detalhar(UUID idRefeicao, UUID idAlimento, Usuario usuarioLogado) {
        var id = new RefeicaoAlimento.idRefeicaoAlimento(idRefeicao, idAlimento);
        var refeicaoAlimento = repository.getReferenceById(id);

        validarIdor(refeicaoAlimento.getRefeicao(), refeicaoAlimento.getAlimento(), usuarioLogado);

        return new DadosDetalhamentoRefeicaoAlimento(refeicaoAlimento);
    }

    public DadosDetalhamentoRefeicaoAlimento atualizar(DadosAtualizacaoRefeicaoAlimento dados, Usuario usuarioLogado) {
        var id = new RefeicaoAlimento.idRefeicaoAlimento(dados.idRefeicao(), dados.idAlimento());
        var refeicaoAlimento = repository.getReferenceById(id);

        validarIdor(refeicaoAlimento.getRefeicao(), refeicaoAlimento.getAlimento(), usuarioLogado);

        refeicaoAlimento.atualizar(dados);
        return new DadosDetalhamentoRefeicaoAlimento(refeicaoAlimento);
    }

    public void deletar(UUID idRefeicao, UUID idAlimento, Usuario usuarioLogado) {
        var id = new RefeicaoAlimento.idRefeicaoAlimento(idRefeicao, idAlimento);
        var refeicaoAlimento = repository.getReferenceById(id);

        validarIdor(refeicaoAlimento.getRefeicao(), refeicaoAlimento.getAlimento(), usuarioLogado);

        repository.delete(refeicaoAlimento);
    }
}
