package com.welltrack.repository.treino;

import com.welltrack.domain.treino.Treino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TreinoRepository extends JpaRepository<Treino, UUID> {

    Page<Treino> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

