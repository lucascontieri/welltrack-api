package com.welltrack.domain.grupomuscular;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "GrupoMuscular")
@Table(name = "GrupoMuscular")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idGrupo")

public class GrupoMuscular {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_grupo")
    private UUID idGrupo;

    @Column(nullable = false, length = 50)
    private String nome_grupo_muscular;

    public void atualizar(com.welltrack.dto.grupomuscular.DadosAtualizacaoGrupoMuscular dados) {
        if (dados.nome_grupo_muscular() != null) {
            this.nome_grupo_muscular = dados.nome_grupo_muscular();
        }
    }
}
