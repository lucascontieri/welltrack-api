package com.welltrack.controller;

import com.welltrack.dto.usuario.DadosAtualizacaoUsuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import com.welltrack.dto.usuario.DadosDetalhamentoUsuario;
import com.welltrack.dto.usuario.DadosListagemUsuario;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.repository.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    // Injetando o repository como sendo um atributo do Controller
    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        var usuario = new Usuario(dados);
        repository.save(usuario);

        var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(usuario.getIdUsuario()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listar (@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemUsuario::new);
        return  ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        var usuario = repository.getReferenceById(dados.idUsuario());
        usuario.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }

    @DeleteMapping("/{idUsuario}")
    @Transactional
    public ResponseEntity deletar(@PathVariable UUID idUsuario){
        var usuario = repository.getReferenceById(idUsuario);
        usuario.inativar();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity detalhar(@PathVariable UUID idUsuario){
        var usuario = repository.getReferenceById(idUsuario);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }
}
