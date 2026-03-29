package com.welltrack.repository.lista;

import com.welltrack.domain.lista.Lista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListaRepository extends JpaRepository<Lista, UUID> {

    Page<Lista> findAllByUsuarioIdUsuario(UUID idUsuario, Pageable pageable);
}

