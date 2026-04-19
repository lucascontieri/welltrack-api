package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.registrotreino.DadosAtualizacaoRegistroTreino;
import com.welltrack.dto.registrotreino.DadosCadastroRegistroTreino;
import com.welltrack.dto.registrotreino.DadosDetalhamentoRegistroTreino;
import com.welltrack.service.registrotreino.RegistroTreinoService;
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
@RequestMapping("registrotreino")
@SecurityRequirement(name = "bearer-key")
public class RegistroTreinoController {

    @Autowired
    private RegistroTreinoService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoRegistroTreino> cadastrar(@RequestBody @Valid DadosCadastroRegistroTreino dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {
        var registroTreino = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/registrotreino/{id}").buildAndExpand(registroTreino.getIdRegistro()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoRegistroTreino(registroTreino));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"dataExecucao"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idRegistro}")
    public ResponseEntity<DadosDetalhamentoRegistroTreino> detalhar(@PathVariable UUID idRegistro, @AuthenticationPrincipal Usuario usuarioLogado) {
        var registroTreino = service.detalhar(idRegistro, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoRegistroTreino(registroTreino));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoRegistroTreino> atualizar(@RequestBody @Valid DadosAtualizacaoRegistroTreino dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var registroTreino = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoRegistroTreino(registroTreino));
    }

    @DeleteMapping("/{idRegistro}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable UUID idRegistro, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idRegistro, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

