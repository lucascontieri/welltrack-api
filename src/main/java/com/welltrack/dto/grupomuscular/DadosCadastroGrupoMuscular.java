package com.welltrack.dto.grupomuscular;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroGrupoMuscular(

        @NotBlank
        String nome_grupo_muscular) {
}

