package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.exercicio.DadosAtualizacaoExercicio;
import com.welltrack.dto.exercicio.DadosCadastroExercicio;
import com.welltrack.dto.exercicio.DadosDetalhamentoExercicio;
import com.welltrack.service.exercicio.ExercicioService;
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
@RequestMapping("exercicio")
@SecurityRequirement(name = "bearer-key")
public class ExercicioController {

    @Autowired
    private ExercicioService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoExercicio> cadastrar(@RequestBody @Valid DadosCadastroExercicio dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder) {
        var exercicio = service.cadastrar(dados, usuarioLogado);

        var uri = uriBuilder.path("/exercicio/{id}").buildAndExpand(exercicio.getIdExercicio()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoExercicio(exercicio));
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam UUID idUsuario,
                                 @AuthenticationPrincipal Usuario usuarioLogado,
                                 @PageableDefault(size = 10, sort = {"nomeExercicio"}) Pageable paginacao) {
        var page = service.listar(idUsuario, paginacao, usuarioLogado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{idExercicio}")
    public ResponseEntity<DadosDetalhamentoExercicio> detalhar(@PathVariable UUID idExercicio, @AuthenticationPrincipal Usuario usuarioLogado) {
        var exercicio = service.detalhar(idExercicio, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoExercicio(exercicio));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoExercicio> atualizar(@RequestBody @Valid DadosAtualizacaoExercicio dados, @AuthenticationPrincipal Usuario usuarioLogado) {
        var exercicio = service.atualizar(dados, usuarioLogado);
        return ResponseEntity.ok(new DadosDetalhamentoExercicio(exercicio));
    }

    @DeleteMapping("/{idExercicio}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable UUID idExercicio, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.deletar(idExercicio, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

