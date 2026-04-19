package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.lista.DadosAtualizacaoLista;
import com.welltrack.dto.lista.DadosCadastroLista;
import com.welltrack.dto.lista.DadosDetalhamentoLista;
import com.welltrack.service.lista.ListaService;
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
@RequestMapping("lista")
@SecurityRequirement(name = "bearer-key")
public class ListaController {

    @Autowired
    private ListaService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoLista> cadastrar(@RequestBody @Valid DadosCadastroLista dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {

        var lista = service.cadastrar(dados, usuarioLogado);
        var uri = uriBuilder.path("/lista/{id}").buildAndExpand(lista.getId_lista()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoLista(lista));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"nomeLista"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id_lista}")
    public ResponseEntity<DadosDetalhamentoLista> detalhar(@PathVariable UUID id_lista, @AuthenticationPrincipal Usuario usuarioLogado) {
        var lista = service.detalhar(id_lista, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoLista(lista));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoLista> atualizar(@RequestBody @Valid DadosAtualizacaoLista dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var lista = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoLista(lista));
    }

    @DeleteMapping("/{id_lista}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable UUID id_lista, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(id_lista, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

