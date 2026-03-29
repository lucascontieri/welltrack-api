package com.welltrack.repository.treinoexercicio;

import com.welltrack.domain.treinoexercicio.TreinoExercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TreinoExercicioRepository extends JpaRepository<TreinoExercicio, TreinoExercicio.idTreinoExercicio> {

    Page<TreinoExercicio> findAllByTreinoUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

