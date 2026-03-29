package com.welltrack.dto.refeicaoalimento;

import com.welltrack.domain.refeicaoalimento.RefeicaoAlimento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record DadosDetalhamentoRefeicaoAlimento(
        UUID idRefeicao,
        UUID idAlimento,
        String nomeAlimento,
        BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal caloriasCalculadas,
        BigDecimal proteinaCalculada,
        BigDecimal carboidratoCalculado,
        BigDecimal gorduraCalculada
) {
    public DadosDetalhamentoRefeicaoAlimento(RefeicaoAlimento entidade) {
        this(
                entidade.getRefeicao().getIdRefeicao(),
                entidade.getAlimento().getIdAlimento(),
                entidade.getAlimento().getNomeAlimento(),
                entidade.getQuantidade(),
                entidade.getUnidadeMedida(),
                calcular(entidade.getAlimento().getCalorias(), entidade.getQuantidade(), entidade.getAlimento().getPesoPorcao()),
                calcular(entidade.getAlimento().getProteina(), entidade.getQuantidade(), entidade.getAlimento().getPesoPorcao()),
                calcular(entidade.getAlimento().getCarboidrato(), entidade.getQuantidade(), entidade.getAlimento().getPesoPorcao()),
                calcular(entidade.getAlimento().getGordura(), entidade.getQuantidade(), entidade.getAlimento().getPesoPorcao())
        );
    }

    private static BigDecimal calcular(BigDecimal valorPorPorcao, BigDecimal quantidadeInformada, BigDecimal pesoPorcao) {
        if (valorPorPorcao == null || pesoPorcao == null || pesoPorcao.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // macros = (valor_por_porcao * quantidade_informada) / peso_porcao
        return valorPorPorcao.multiply(quantidadeInformada)
                .divide(pesoPorcao, 2, RoundingMode.HALF_UP);
    }
}
