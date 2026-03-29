package com.welltrack.dto.exercicio;

import com.welltrack.domain.exercicio.Exercicio;

import java.util.UUID;

public record DadosListagemExercicio(
        UUID idExercicio,
        String nomeExercicio,
        String imagemExercicio,
        String nomeGrupoMuscular) {

    public DadosListagemExercicio(Exercicio exercicio) {
        this(exercicio.getIdExercicio(),
                exercicio.getNomeExercicio(),
                exercicio.getImagemExercicio(),
                exercicio.getGrupoMuscular().getNome_grupo_muscular());
    }
}

