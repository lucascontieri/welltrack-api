package com.welltrack.repository.grupomuscular;

import com.welltrack.domain.grupomuscular.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, UUID> {
}

