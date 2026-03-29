package com.welltrack.domain.treino;

import com.welltrack.domain.registrotreino.RegistroTreino;
import com.welltrack.domain.treinoexercicio.TreinoExercicio;
import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Treino")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTreino")
public class Treino {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_treino")
    private UUID idTreino;

    @Column(nullable = false, length = 50)
    private String nomeTreino;

    private LocalDate dataTreino;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL)
    private List<TreinoExercicio> exercicios;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL)
    private List<RegistroTreino> registros;

    public void atualizar(com.welltrack.dto.treino.DadosAtualizacaoTreino dados) {
        if (dados.nomeTreino() != null) {
            this.nomeTreino = dados.nomeTreino();
        }

        if (dados.dataTreino() != null) {
            this.dataTreino = dados.dataTreino();
        }
    }
}
