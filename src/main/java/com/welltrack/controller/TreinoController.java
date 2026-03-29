package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.treino.DadosAtualizacaoTreino;
import com.welltrack.dto.treino.DadosCadastroTreino;
import com.welltrack.dto.treino.DadosDetalhamentoTreino;
import com.welltrack.service.treino.TreinoService;
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
@RequestMapping("treino")
@SecurityRequirement(name = "bearer-key")
public class TreinoController {

    @Autowired
    private TreinoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroTreino dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {

        var treino = service.cadastrar(dados, usuarioLogado);
        var uri = uriBuilder.path("/treino/{id}").buildAndExpand(treino.getIdTreino()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoTreino(treino));
    }

    @GetMapping
    public ResponseEntity listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"nomeTreino"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idTreino}")
    public ResponseEntity detalhar(@PathVariable UUID idTreino, @AuthenticationPrincipal Usuario usuarioLogado) {
        var treino = service.detalhar(idTreino, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTreino(treino));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoTreino dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var treino = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTreino(treino));
    }

    @DeleteMapping("/{idTreino}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idTreino, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idTreino, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

