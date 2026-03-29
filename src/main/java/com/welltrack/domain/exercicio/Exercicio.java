package com.welltrack.domain.exercicio;

import com.welltrack.domain.grupomuscular.GrupoMuscular;
import com.welltrack.domain.treinoexercicio.TreinoExercicio;
import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Exercicio")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idExercicio")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_exercicio")
    private UUID idExercicio;

    @Column(nullable = false, length = 150)
    private String nomeExercicio;

    @Column(length = 500)
    private String imagemExercicio;

    @Column(length = 500)
    private String videoExercicio;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private GrupoMuscular grupoMuscular;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "exercicio", cascade = CascadeType.ALL)
    private List<TreinoExercicio> treinos;

    public void atualizar(com.welltrack.dto.exercicio.DadosAtualizacaoExercicio dados) {
        if (dados.nomeExercicio() != null) {
            this.nomeExercicio = dados.nomeExercicio();
        }

        if (dados.imagemExercicio() != null) {
            this.imagemExercicio = dados.imagemExercicio();
        }

        if (dados.videoExercicio() != null) {
            this.videoExercicio = dados.videoExercicio();
        }

        if (dados.idGrupo() != null) {
            this.grupoMuscular = new GrupoMuscular(dados.idGrupo(), null);
        }
    }
}