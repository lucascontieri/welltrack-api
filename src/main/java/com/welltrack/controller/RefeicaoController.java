package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.refeicao.DadosAtualizacaoRefeicao;
import com.welltrack.dto.refeicao.DadosCadastroRefeicao;
import com.welltrack.dto.refeicao.DadosDetalhamentoRefeicao;
import com.welltrack.service.refeicao.RefeicaoService;
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
@RequestMapping("refeicao")
@SecurityRequirement(name = "bearer-key")
public class RefeicaoController {

    @Autowired
    private RefeicaoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroRefeicao dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {

        var refeicao = service.cadastrar(dados, usuarioLogado);
        var uri = uriBuilder.path("/refeicao/{id}").buildAndExpand(refeicao.getIdRefeicao()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoRefeicao(refeicao));
    }

    @GetMapping
    public ResponseEntity listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"nomeRefeicao"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idRefeicao}")
    public ResponseEntity detalhar(@PathVariable UUID idRefeicao, @AuthenticationPrincipal Usuario usuarioLogado) {
        var refeicao = service.detalhar(idRefeicao, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoRefeicao(refeicao));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoRefeicao dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var refeicao = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoRefeicao(refeicao));
    }

    @DeleteMapping("/{idRefeicao}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idRefeicao, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idRefeicao, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

