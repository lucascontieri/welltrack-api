package com.welltrack.service.lista;

import com.welltrack.domain.lista.Lista;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.lista.DadosAtualizacaoLista;
import com.welltrack.dto.lista.DadosCadastroLista;
import com.welltrack.dto.lista.DadosListagemLista;
import com.welltrack.repository.lista.ListaRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListaService {

    @Autowired
    private ListaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void validarIdor(UUID idAlvo, Usuario usuarioLogado) {
        if (!idAlvo.equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Acesso negado: O recurso solicitado pertence a outro usuário.");
        }
    }

    public Lista cadastrar(DadosCadastroLista dados, Usuario usuarioLogado) {
        validarIdor(dados.idUsuario(), usuarioLogado);

        var usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        var lista = new Lista(null, dados.nomeLista(), usuario, null);
        return repository.save(lista);
    }

    public Page<DadosListagemLista> listar(UUID idUsuario, Pageable paginacao, Usuario usuarioLogado) {
        validarIdor(idUsuario, usuarioLogado);
        return repository.findAllByUsuarioIdUsuario(idUsuario, paginacao).map(DadosListagemLista::new);
    }

    public Lista detalhar(UUID id_lista, Usuario usuarioLogado) {
        var lista = repository.getReferenceById(id_lista);
        validarIdor(lista.getUsuario().getIdUsuario(), usuarioLogado);
        return lista;
    }

    public Lista atualizar(DadosAtualizacaoLista dados, Usuario usuarioLogado) {
        var lista = repository.getReferenceById(dados.id_lista());
        validarIdor(lista.getUsuario().getIdUsuario(), usuarioLogado);
        lista.atualizar(dados);
        return lista;
    }

    public void deletar(UUID id_lista, Usuario usuarioLogado) {
        var lista = repository.getReferenceById(id_lista);
        validarIdor(lista.getUsuario().getIdUsuario(), usuarioLogado);
        repository.delete(lista);
    }
}
