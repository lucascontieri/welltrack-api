package com.welltrack.repository.tarefa;

import com.welltrack.domain.tarefa.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {

    Page<Tarefa> findAllByListaUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

