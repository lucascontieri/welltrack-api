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
@EqualsAndHashCode
public class RefeicaoAlimento {

    @EmbeddedId
    private idRefeicaoAlimento id;

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

    public void atualizar(com.welltrack.dto.refeicaoalimento.DadosAtualizacaoRefeicaoAlimento dados) {
        if (dados.quantidade() != null) {
            this.quantidade = dados.quantidade();
        }

        if (dados.unidadeMedida() != null) {
            this.unidadeMedida = dados.unidadeMedida();
        }
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class idRefeicaoAlimento implements Serializable {

        @Column(name = "id_refeicao")
        private UUID idRefeicao;

        @Column(name = "id_alimento")
        private UUID idAlimento;
    }
}
