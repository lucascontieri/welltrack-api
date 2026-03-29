package com.welltrack.repository.refeicao;

import com.welltrack.domain.refeicao.Refeicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefeicaoRepository extends JpaRepository<Refeicao, UUID> {

    Page<Refeicao> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

