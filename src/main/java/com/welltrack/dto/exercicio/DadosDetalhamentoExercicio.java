package com.welltrack.dto.exercicio;

import com.welltrack.domain.exercicio.Exercicio;

import java.util.UUID;

public record DadosDetalhamentoExercicio(
        UUID idExercicio,
        String nomeExercicio,
        String imagemExercicio,
        String videoExercicio,
        UUID idGrupo,
        String nomeGrupoMuscular,
        UUID idUsuario) {

    public DadosDetalhamentoExercicio(Exercicio exercicio) {
        this(exercicio.getIdExercicio(),
                exercicio.getNomeExercicio(),
                exercicio.getImagemExercicio(),
                exercicio.getVideoExercicio(),
                exercicio.getGrupoMuscular().getIdGrupo(),
                exercicio.getGrupoMuscular().getNome_grupo_muscular(),
                exercicio.getUsuario().getIdUsuario());
    }
}

