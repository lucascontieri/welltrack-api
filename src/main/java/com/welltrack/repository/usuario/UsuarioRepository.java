package com.welltrack.repository.usuario;

import com.welltrack.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Page<Usuario> findAllByAtivoTrue(Pageable pageable);

    UserDetails findByEmail(String email);

    Optional<Usuario> findByGoogleId(String googleId);

    Optional<Usuario> findOptionalByEmail(String email);
}
