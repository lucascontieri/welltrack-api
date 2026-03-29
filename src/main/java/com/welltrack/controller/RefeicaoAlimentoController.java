package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.refeicaoalimento.DadosAtualizacaoRefeicaoAlimento;
import com.welltrack.dto.refeicaoalimento.DadosCadastroRefeicaoAlimento;
import com.welltrack.service.refeicaoalimento.RefeicaoAlimentoService;
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
@RequestMapping("refeicao-alimento")
@SecurityRequirement(name = "bearer-key")
public class RefeicaoAlimentoController {

    @Autowired
    private RefeicaoAlimentoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroRefeicaoAlimento dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {

        var dto = service.cadastrar(dados, usuarioLogado);
        var uri = uriBuilder.path("/refeicao-alimento/{idRefeicao}/{idAlimento}")
                .buildAndExpand(dto.idRefeicao(), dto.idAlimento()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10) Pageable paginacao) {
        var page = service.listarPorUsuario(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idRefeicao}/{idAlimento}")
    public ResponseEntity detalhar(@PathVariable UUID idRefeicao,
                                   @PathVariable UUID idAlimento,
                                   @AuthenticationPrincipal Usuario usuarioLogado) {
        var dto = service.detalhar(idRefeicao, idAlimento, usuarioLogado);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoRefeicaoAlimento dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado) {
        var dto = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{idRefeicao}/{idAlimento}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idRefeicao,
                                  @PathVariable UUID idAlimento,
                                  @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idRefeicao, idAlimento, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
