package com.welltrack.service.integracao;

import com.welltrack.dto.alimento.DadosAlimentoPorCodigoDeBarras;
import com.welltrack.dto.integracao.OpenFoodFactsResponse;
import com.welltrack.exception.ValidacaoException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL_API = "https://world.openfoodfacts.org/api/v0/product/{barcode}.json";

    public DadosAlimentoPorCodigoDeBarras buscarPorCodigoDeBarras(String barcode) {
        try {
            var response = restTemplate.getForObject(URL_API, OpenFoodFactsResponse.class, barcode);

            if (response == null || response.status() == null || response.status() == 0 || response.product() == null) {
                throw new ValidacaoException("Produto não encontrado para o código de barras informado.");
            }

            var product = response.product();
            var nutriments = product.nutriments();

            if (nutriments == null) {
                throw new ValidacaoException("Informações nutricionais não disponíveis para o produto.");
            }

            return new DadosAlimentoPorCodigoDeBarras(
                    product.productName() != null && !product.productName().isBlank() ? product.productName() : "Produto sem nome cadastrado",
                    tratarValor(nutriments.carbohydrates100g()),
                    tratarValor(nutriments.proteins100g()),
                    tratarValor(nutriments.fat100g()),
                    tratarValor(nutriments.energyKcal100g()),
                    "g",
                    new BigDecimal("100.00"),
                    product.imageUrl()
            );

        } catch (HttpClientErrorException.NotFound e) {
            throw new ValidacaoException("Produto não encontrado para o código de barras informado na API.");
        } catch (Exception e) {
            if (e instanceof ValidacaoException) {
                throw e;
            }
            throw new RuntimeException("Erro ao buscar informações nutricionais do produto: " + e.getMessage());
        }
    }

    private BigDecimal tratarValor(Double valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(valor);
    }
}
