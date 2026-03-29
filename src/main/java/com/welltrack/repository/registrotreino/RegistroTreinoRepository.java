package com.welltrack.repository.registrotreino;

import com.welltrack.domain.registrotreino.RegistroTreino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistroTreinoRepository extends JpaRepository<RegistroTreino, UUID> {

    Page<RegistroTreino> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

