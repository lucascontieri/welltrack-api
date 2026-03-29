package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.alimento.DadosAtualizacaoAlimento;
import com.welltrack.dto.alimento.DadosCadastroAlimento;
import com.welltrack.dto.alimento.DadosDetalhamentoAlimento;
import com.welltrack.service.alimento.AlimentoService;
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
@RequestMapping("alimento")
@SecurityRequirement(name = "bearer-key")
public class AlimentoController {

    @Autowired
    private AlimentoService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroAlimento dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {

        var alimento = service.cadastrar(dados, usuarioLogado);
        var uri = uriBuilder.path("/alimento/{id}").buildAndExpand(alimento.getIdAlimento()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoAlimento(alimento));
    }

    @GetMapping
    public ResponseEntity listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"nomeAlimento"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idAlimento}")
    public ResponseEntity detalhar(@PathVariable UUID idAlimento, @AuthenticationPrincipal Usuario usuarioLogado) {
        var alimento = service.detalhar(idAlimento, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoAlimento(alimento));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoAlimento dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var alimento = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoAlimento(alimento));
    }

    @DeleteMapping("/{idAlimento}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idAlimento, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idAlimento, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

