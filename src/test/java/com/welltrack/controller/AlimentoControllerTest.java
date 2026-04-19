package com.welltrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.alimento.DadosCadastroAlimento;
import com.welltrack.service.alimento.AlimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "MAIL_HOST=localhost",
        "MAIL_PORT=587",
        "MAIL_USERNAME=user",
        "MAIL_PASSWORD=password",
        "MAIL_FROM=noreply@welltrack.com",
        "JWT_SECRET=321654",
        "GOOGLE_CLIENT_ID=google",
        "GEMINI_API_KEY=gemini",
        "APP_PUBLIC_BASE_URL=http://localhost:8080",
        "TEST_DATASOURCE_URL=jdbc:postgresql://localhost:5432/welltrack_db_test"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlimentoService alimentoService;

    private Usuario usuarioLogado;
    private UUID idUsuario = UUID.randomUUID();

    @BeforeEach
    void setup() {
        usuarioLogado = new Usuario();
        // Nao podemos expor o setter, mas podemos mockar o contexto do Spring Security
    }

    @Test
    @DisplayName("Deve retornar 201 Created quando o JSON for valido")
    void cadastrar_Cenario1() throws Exception {
        // Arrange
        var dados = new DadosCadastroAlimento(
                "Arroz", BigDecimal.valueOf(20), BigDecimal.valueOf(2),
                BigDecimal.valueOf(0), "g", BigDecimal.valueOf(50), 
                BigDecimal.valueOf(100), null, idUsuario
        );

        Alimento mockRetorno = new Alimento(UUID.randomUUID(), dados.nomeAlimento(), dados.carboidrato(),
                dados.proteina(), dados.gordura(), dados.unidadePadrao(), dados.pesoPorcao(),
                dados.calorias(), dados.imagemAlimento(), usuarioLogado);

        when(alimentoService.cadastrar(any(), any())).thenReturn(mockRetorno);

        // Act & Assert
        mockMvc.perform(post("/alimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados))
                        .with(user(usuarioLogado)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request ao tentar cadastrar calorias negativas")
    void cadastrar_CenarioCaloriasNegativas() throws Exception {
        // Arrange
        var dadosCaloriaNegativa = new DadosCadastroAlimento(
                "Arroz", BigDecimal.valueOf(20), BigDecimal.valueOf(2),
                BigDecimal.valueOf(0), "g", BigDecimal.valueOf(50), 
                BigDecimal.valueOf(-100), null, idUsuario
        );

        // Act & Assert
        mockMvc.perform(post("/alimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCaloriaNegativa))
                        .with(user(usuarioLogado)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request ao tentar cadastrar carboidrato negativo")
    void cadastrar_CenarioCarboidratoNegativo() throws Exception {
        // Arrange
        var dadosCarboNegativo = new DadosCadastroAlimento(
                "Arroz", BigDecimal.valueOf(-20), BigDecimal.valueOf(2),
                BigDecimal.valueOf(0), "g", BigDecimal.valueOf(50), 
                BigDecimal.valueOf(100), null, idUsuario
        );

        // Act & Assert
        mockMvc.perform(post("/alimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCarboNegativo))
                        .with(user(usuarioLogado)))
                .andExpect(status().isBadRequest());
    }
}
