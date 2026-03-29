package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import com.welltrack.dto.usuario.DadosDetalhamentoUsuario;
import com.welltrack.service.usuario.UsuarioService;
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
@RequestMapping("usuario")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        var usuario = service.cadastrar(dados);
        var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(usuario.getIdUsuario()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = service.listar(paginacao);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var usuario = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }

    @DeleteMapping("/{idUsuario}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idUsuario, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idUsuario, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity detalhar(@PathVariable UUID idUsuario, @AuthenticationPrincipal Usuario usuarioLogado) {
        var usuario = service.detalhar(idUsuario, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }
}
