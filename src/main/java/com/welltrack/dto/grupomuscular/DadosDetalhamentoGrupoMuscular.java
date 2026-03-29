package com.welltrack.dto.grupomuscular;

import com.welltrack.domain.grupomuscular.GrupoMuscular;

import java.util.UUID;

public record DadosDetalhamentoGrupoMuscular(
        UUID idGrupo,
        String nome_grupo_muscular) {

    public DadosDetalhamentoGrupoMuscular(GrupoMuscular grupoMuscular) {
        this(grupoMuscular.getIdGrupo(),
                grupoMuscular.getNome_grupo_muscular());
    }
}

