package com.welltrack.repository.tarefabloco;

import com.welltrack.domain.tarefabloco.TarefaBloco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TarefaBlocoRepository extends JpaRepository<TarefaBloco, UUID> {

    Page<TarefaBloco> findAllByTarefaListaUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

