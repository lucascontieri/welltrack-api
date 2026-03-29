package com.welltrack.repository.alimento;

import com.welltrack.domain.alimento.Alimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlimentoRepository extends JpaRepository<Alimento, UUID> {

    Page<Alimento> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

