package com.welltrack.service.grupomuscular;

import com.welltrack.domain.grupomuscular.GrupoMuscular;
import com.welltrack.domain.usuario.TipoUsuario;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.grupomuscular.DadosAtualizacaoGrupoMuscular;
import com.welltrack.dto.grupomuscular.DadosCadastroGrupoMuscular;
import com.welltrack.dto.grupomuscular.DadosListagemGrupoMuscular;
import com.welltrack.repository.grupomuscular.GrupoMuscularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GrupoMuscularService {

    @Autowired
    private GrupoMuscularRepository repository;

    private void validarAdministrador(Usuario usuarioLogado) {
        if (!TipoUsuario.ADMIN.equals(usuarioLogado.getTipoUsuario())) {
            throw new AccessDeniedException("Acesso negado: Apenas administradores podem modificar grupos musculares.");
        }
    }

    public GrupoMuscular cadastrar(DadosCadastroGrupoMuscular dados, Usuario usuarioLogado) {
        validarAdministrador(usuarioLogado);
        var grupoMuscular = new GrupoMuscular(null, dados.nome_grupo_muscular());
        return repository.save(grupoMuscular);
    }

    public Page<DadosListagemGrupoMuscular> listar(Pageable paginacao) {
        // Listagem é pública/comum
        return repository.findAll(paginacao).map(DadosListagemGrupoMuscular::new);
    }

    public GrupoMuscular detalhar(UUID idGrupo) {
        return repository.getReferenceById(idGrupo);
    }

    public GrupoMuscular atualizar(DadosAtualizacaoGrupoMuscular dados, Usuario usuarioLogado) {
        validarAdministrador(usuarioLogado);
        var grupoMuscular = repository.getReferenceById(dados.idGrupo());
        grupoMuscular.atualizar(dados);
        return grupoMuscular;
    }

    public void deletar(UUID idGrupo, Usuario usuarioLogado) {
        validarAdministrador(usuarioLogado);
        repository.deleteById(idGrupo);
    }
}
