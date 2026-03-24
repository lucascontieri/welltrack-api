package com.welltrack.domain.refeicaoalimento;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.refeicao.Refeicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "RefeicaoAlimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idRefeicaoAlimento")
public class RefeicaoAlimento {

    @EmbeddedId
    private RefeicaoAlimentoId id;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal quantidade;

    @Column(nullable = false, length = 20)
    private String unidadeMedida;

    @ManyToOne
    @MapsId("idRefeicao")
    @JoinColumn(name = "id_refeicao")
    private Refeicao refeicao;

    @ManyToOne
    @MapsId("idAlimento")
    @JoinColumn(name = "id_alimento")
    private Alimento alimento;


    @Embeddable
    public static class RefeicaoAlimentoId implements Serializable {

        @Column(name = "id_refeicao")
        private UUID idRefeicao;

        @Column(name = "id_alimento")
        private UUID idAlimento;
    }
}
