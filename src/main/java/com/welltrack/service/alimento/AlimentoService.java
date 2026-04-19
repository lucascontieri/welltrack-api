package com.welltrack.service.alimento;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.alimento.DadosAtualizacaoAlimento;
import com.welltrack.dto.alimento.DadosCadastroAlimento;
import com.welltrack.dto.alimento.DadosListagemAlimento;
import com.welltrack.repository.alimento.AlimentoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlimentoService {

    @Autowired
    private AlimentoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarUsuario(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Alimento cadastrar(DadosCadastroAlimento dados, Usuario usuarioLogado) {
        validarUsuario(dados.idUsuario(), usuarioLogado);

        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var alimento = new Alimento(null, dados.nomeAlimento(), dados.carboidrato(), dados.proteina(),
                dados.gordura(), dados.unidadePadrao(), dados.pesoPorcao(), dados.calorias(),
                dados.imagemAlimento(), usuario);
        return repository.save(alimento);
    }

    public Page<DadosListagemAlimento> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarUsuario(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemAlimento::new);
    }

    public Alimento detalhar(UUID idAlimento, Usuario usuarioLogado) {
        var alimento = repository.getReferenceById(idAlimento);
        validarUsuario(alimento.getUsuario().getIdUsuario(), usuarioLogado);
        return alimento;
    }

    public Alimento atualizar(DadosAtualizacaoAlimento dados, Usuario usuarioLogado) {
        var alimento = repository.getReferenceById(dados.idAlimento());
        validarUsuario(alimento.getUsuario().getIdUsuario(), usuarioLogado);
        alimento.atualizar(dados);
        return alimento;
    }

    public void deletar(UUID idAlimento, Usuario usuarioLogado) {
        var alimento = repository.getReferenceById(idAlimento);
        validarUsuario(alimento.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(alimento);
    }
}
