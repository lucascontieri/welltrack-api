package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.grupomuscular.DadosAtualizacaoGrupoMuscular;
import com.welltrack.dto.grupomuscular.DadosCadastroGrupoMuscular;
import com.welltrack.dto.grupomuscular.DadosDetalhamentoGrupoMuscular;
import com.welltrack.service.grupomuscular.GrupoMuscularService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("grupomuscular")
@SecurityRequirement(name = "bearer-key")
public class GrupoMuscularController {

    @Autowired
    private GrupoMuscularService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroGrupoMuscular dados, @AuthenticationPrincipal Usuario usuarioLogado, UriComponentsBuilder uriBuilder) {
        var grupoMuscular = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/grupomuscular/{id}").buildAndExpand(grupoMuscular.getIdGrupo()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoGrupoMuscular(grupoMuscular));
    }

    @GetMapping
    public ResponseEntity listar(@PageableDefault(size = 10, sort = {"nome_grupo_muscular"}) Pageable paginacao) {
        var page = service.listar(paginacao);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idGrupo}")
    public ResponseEntity detalhar(@PathVariable UUID idGrupo) {
        var grupoMuscular = service.detalhar(idGrupo);
        return ResponseEntity.ok(new DadosDetalhamentoGrupoMuscular(grupoMuscular));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoGrupoMuscular dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var grupoMuscular = service.atualizar(dados, usuarioLogado);

        return ResponseEntity.ok(new DadosDetalhamentoGrupoMuscular(grupoMuscular));
    }

    @DeleteMapping("/{idGrupo}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idGrupo, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idGrupo, usuarioLogado);

        return ResponseEntity.noContent().build();
    }
}

