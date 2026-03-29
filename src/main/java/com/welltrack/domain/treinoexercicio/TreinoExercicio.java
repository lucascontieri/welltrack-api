package com.welltrack.domain.treinoexercicio;

import com.welltrack.domain.exercicio.Exercicio;
import com.welltrack.domain.treino.Treino;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "TreinoExercicio")
@Table(name = "TreinoExercicio")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTreinoExercicio")
public class TreinoExercicio {

    @EmbeddedId
    private idTreinoExercicio id;

    @Column
    private Integer serieExercicio;

    @Column
    private Integer descanso;

    @Column(precision = 5, scale = 2)
    private BigDecimal carga;

    @Column
    private Integer repeticoes;

    @Column
    private Integer ordem;

    @ManyToOne
    @MapsId("idTreino")
    @JoinColumn(name = "id_treino")
    private Treino treino;

    @ManyToOne
    @MapsId("idExercicio")
    @JoinColumn(name = "id_exercicio")
    private Exercicio exercicio;

    public void atualizar(com.welltrack.dto.treinoexercicio.DadosAtualizacaoTreinoExercicio dados) {
        if (dados.serieExercicio() != null) {
            this.serieExercicio = dados.serieExercicio();
        }

        if (dados.descanso() != null) {
            this.descanso = dados.descanso();
        }

        if (dados.carga() != null) {
            this.carga = dados.carga();
        }

        if (dados.repeticoes() != null) {
            this.repeticoes = dados.repeticoes();
        }

        if (dados.ordem() != null) {
            this.ordem = dados.ordem();
        }
    }

    @Embeddable
    public static class idTreinoExercicio implements Serializable {

        @Column(name = "id_treino")
        private UUID idTreino;

        @Column(name = "id_exercicio")
        private UUID idExercicio;
    }
}
