package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tarefa.DadosAtualizacaoTarefa;
import com.welltrack.dto.tarefa.DadosCadastroTarefa;
import com.welltrack.dto.tarefa.DadosDetalhamentoTarefa;
import com.welltrack.service.tarefa.TarefaService;
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
@RequestMapping("tarefa")
@SecurityRequirement(name = "bearer-key")
public class TarefaController {

    @Autowired
    private TarefaService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTarefa> cadastrar(@RequestBody @Valid DadosCadastroTarefa dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {
        var tarefa = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/tarefa/{id}").buildAndExpand(tarefa.getIdTarefa()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoTarefa(tarefa));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"prazoMaximo"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idTarefa}")
    public ResponseEntity<DadosDetalhamentoTarefa> detalhar(@PathVariable UUID idTarefa, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tarefa = service.detalhar(idTarefa, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTarefa(tarefa));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTarefa> atualizar(@RequestBody @Valid DadosAtualizacaoTarefa dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tarefa = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTarefa(tarefa));
    }

    @DeleteMapping("/{idTarefa}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable UUID idTarefa, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idTarefa, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

