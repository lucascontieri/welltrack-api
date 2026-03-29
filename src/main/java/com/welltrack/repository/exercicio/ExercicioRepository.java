package com.welltrack.repository.exercicio;

import com.welltrack.domain.exercicio.Exercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExercicioRepository extends JpaRepository<Exercicio, UUID> {

    Page<Exercicio> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

