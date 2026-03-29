package com.welltrack.repository.refeicaoalimento;

import com.welltrack.domain.refeicaoalimento.RefeicaoAlimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefeicaoAlimentoRepository extends JpaRepository<RefeicaoAlimento, RefeicaoAlimento.idRefeicaoAlimento> {

    Page<RefeicaoAlimento> findAllByRefeicaoUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

