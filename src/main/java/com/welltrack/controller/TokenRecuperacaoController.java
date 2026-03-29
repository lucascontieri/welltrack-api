package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.tokenrecuperacao.DadosAtualizacaoTokenRecuperacao;
import com.welltrack.dto.tokenrecuperacao.DadosCadastroTokenRecuperacao;
import com.welltrack.dto.tokenrecuperacao.DadosDetalhamentoTokenRecuperacao;
import com.welltrack.service.tokenrecuperacao.TokenRecuperacaoService;
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
@RequestMapping("tokenrecuperacao")
@SecurityRequirement(name = "bearer-key")
public class TokenRecuperacaoController {

    @Autowired
    private TokenRecuperacaoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroTokenRecuperacao dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {
        var tokenRecuperacao = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/tokenrecuperacao/{id}").buildAndExpand(tokenRecuperacao.getIdToken()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoTokenRecuperacao(tokenRecuperacao));
    }

    @GetMapping
    public ResponseEntity listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idToken}")
    public ResponseEntity detalhar(@PathVariable UUID idToken, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tokenRecuperacao = service.detalhar(idToken, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTokenRecuperacao(tokenRecuperacao));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoTokenRecuperacao dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var tokenRecuperacao = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoTokenRecuperacao(tokenRecuperacao));
    }

    @DeleteMapping("/{idToken}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idToken, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idToken, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

