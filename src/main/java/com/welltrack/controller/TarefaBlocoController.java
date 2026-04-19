package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tarefabloco.DadosAtualizacaoTarefaBloco;
import com.welltrack.dto.tarefabloco.DadosCadastroTarefaBloco;
import com.welltrack.dto.tarefabloco.DadosDetalhamentoTarefaBloco;
import com.welltrack.service.tarefabloco.TarefaBlocoService;
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
@RequestMapping("tarefabloco")
@SecurityRequirement(name = "bearer-key")
public class TarefaBlocoController {

    @Autowired
    private TarefaBlocoService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTarefaBloco> cadastrar(@RequestBody @Valid DadosCadastroTarefaBloco dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {
        var tarefaBloco = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/tarefabloco/{id}").buildAndExpand(tarefaBloco.getIdBloco()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoTarefaBloco(tarefaBloco));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"ordem"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idBloco}")
    public ResponseEntity<DadosDetalhamentoTarefaBloco> detalhar(@PathVariable UUID idBloco, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tarefaBloco = service.detalhar(idBloco, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTarefaBloco(tarefaBloco));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTarefaBloco> atualizar(@RequestBody @Valid DadosAtualizacaoTarefaBloco dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tarefaBloco = service.atualizar(dados, usuarioLogado);

        return ResponseEntity.ok(new DadosDetalhamentoTarefaBloco(tarefaBloco));
    }

    @DeleteMapping("/{idBloco}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable UUID idBloco, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idBloco, usuarioLogado);

        return ResponseEntity.noContent().build();
    }
}

