package com.welltrack.domain.alimento;

import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "Alimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idAlimento")
public class Alimento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_alimento")
    private UUID idAlimento;

    @Column(length = 150)
    private String nomeAlimento;

    @Column(precision = 6, scale = 2)
    private BigDecimal carboidrato;

    @Column(precision = 6, scale = 2)
    private BigDecimal proteina;

    @Column(precision = 6, scale = 2)
    private BigDecimal gordura;

    @Column(nullable = false, length = 20)
    private String unidadePadrao;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal pesoPorcao;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal calorias;

    @Column(length = 500)
    private String imagemAlimento;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public void atualizar(com.welltrack.dto.alimento.DadosAtualizacaoAlimento dados) {
        if (dados.nomeAlimento() != null) {
            this.nomeAlimento = dados.nomeAlimento();
        }

        if (dados.carboidrato() != null) {
            this.carboidrato = dados.carboidrato();
        }

        if (dados.proteina() != null) {
            this.proteina = dados.proteina();
        }

        if (dados.gordura() != null) {
            this.gordura = dados.gordura();
        }

        if (dados.unidadePadrao() != null) {
            this.unidadePadrao = dados.unidadePadrao();
        }

        if (dados.pesoPorcao() != null) {
            this.pesoPorcao = dados.pesoPorcao();
        }

        if (dados.calorias() != null) {
            this.calorias = dados.calorias();
        }

        if (dados.imagemAlimento() != null) {
            this.imagemAlimento = dados.imagemAlimento();
        }
    }
}