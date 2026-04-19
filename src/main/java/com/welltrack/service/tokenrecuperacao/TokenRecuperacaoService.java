package com.welltrack.service.tokenrecuperacao;

import com.welltrack.domain.tokenrecuperacao.TipoTokenRecuperacao;
import com.welltrack.domain.tokenrecuperacao.TokenRecuperacao;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tokenrecuperacao.DadosAtualizacaoTokenRecuperacao;
import com.welltrack.dto.tokenrecuperacao.DadosCadastroTokenRecuperacao;
import com.welltrack.dto.tokenrecuperacao.DadosListagemTokenRecuperacao;
import com.welltrack.repository.tokenrecuperacao.TokenRecuperacaoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenRecuperacaoService {

    @Autowired
    private TokenRecuperacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarUsuario(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public TokenRecuperacao cadastrar(DadosCadastroTokenRecuperacao dados, Usuario usuarioLogado) {
        validarUsuario(dados.idUsuario(), usuarioLogado);

        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var tipo = dados.tipo() != null ? dados.tipo() : TipoTokenRecuperacao.RECUPERACAO_SENHA;
        var tokenRecuperacao = new TokenRecuperacao(null, dados.token(), dados.expiracao(), dados.usado(),
                dados.tentativas(), dados.dataCriacao(), tipo, usuario);
        return repository.save(tokenRecuperacao);
    }

    public Page<DadosListagemTokenRecuperacao> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarUsuario(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemTokenRecuperacao::new);
    }

    public TokenRecuperacao detalhar(UUID idToken, Usuario usuarioLogado) {
        var tokenRecuperacao = repository.getReferenceById(idToken);
        validarUsuario(tokenRecuperacao.getUsuario().getIdUsuario(), usuarioLogado);
        return tokenRecuperacao;
    }

    public TokenRecuperacao atualizar(DadosAtualizacaoTokenRecuperacao dados, Usuario usuarioLogado) {
        var tokenRecuperacao = repository.getReferenceById(dados.idToken());
        validarUsuario(tokenRecuperacao.getUsuario().getIdUsuario(), usuarioLogado);
        tokenRecuperacao.atualizar(dados);
        return tokenRecuperacao;
    }

    public void deletar(UUID idToken, Usuario usuarioLogado) {
        var tokenRecuperacao = repository.getReferenceById(idToken);
        validarUsuario(tokenRecuperacao.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(tokenRecuperacao);
    }
}
