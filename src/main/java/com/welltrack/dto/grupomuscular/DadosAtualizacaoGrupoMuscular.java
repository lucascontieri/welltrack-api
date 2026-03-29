package com.welltrack.dto.grupomuscular;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosAtualizacaoGrupoMuscular(

        @NotNull
        UUID idGrupo,

        String nome_grupo_muscular) {
}

