package com.welltrack.unit;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.refeicao.Refeicao;
import com.welltrack.domain.refeicaoalimento.RefeicaoAlimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.refeicaoalimento.DadosCadastroRefeicaoAlimento;
import com.welltrack.dto.refeicaoalimento.DadosDetalhamentoRefeicaoAlimento;
import com.welltrack.repository.alimento.AlimentoRepository;
import com.welltrack.repository.refeicao.RefeicaoRepository;
import com.welltrack.repository.refeicaoalimento.RefeicaoAlimentoRepository;
import com.welltrack.service.refeicaoalimento.RefeicaoAlimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefeicaoAlimentoServiceTest {

    @InjectMocks
    private RefeicaoAlimentoService service;

    @Mock
    private RefeicaoAlimentoRepository repository;

    @Mock
    private RefeicaoRepository refeicaoRepository;

    @Mock
    private AlimentoRepository alimentoRepository;

    private Usuario usuarioLogado;
    private Usuario outroUsuario;
    private Refeicao refeicao;
    private Alimento alimento;

    private UUID idUsuarioLogado = UUID.randomUUID();
    private UUID idOutroUsuario = UUID.randomUUID();
    private UUID idRefeicao = UUID.randomUUID();
    private UUID idAlimento = UUID.randomUUID();

    @BeforeEach
    void setup() {
        usuarioLogado = mock(Usuario.class);
        lenient().when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);

        outroUsuario = mock(Usuario.class);
        lenient().when(outroUsuario.getIdUsuario()).thenReturn(idOutroUsuario);

        refeicao = mock(Refeicao.class);
        lenient().when(refeicao.getIdRefeicao()).thenReturn(idRefeicao);
        lenient().when(refeicao.getUsuario()).thenReturn(usuarioLogado);

        alimento = mock(Alimento.class);
        lenient().when(alimento.getIdAlimento()).thenReturn(idAlimento);
        lenient().when(alimento.getNomeAlimento()).thenReturn("Feijão");
        lenient().when(alimento.getUsuario()).thenReturn(usuarioLogado);
        lenient().when(alimento.getCalorias()).thenReturn(BigDecimal.valueOf(100));
        lenient().when(alimento.getProteina()).thenReturn(BigDecimal.valueOf(10));
        lenient().when(alimento.getCarboidrato()).thenReturn(BigDecimal.valueOf(5));
        lenient().when(alimento.getGordura()).thenReturn(BigDecimal.valueOf(2));
        lenient().when(alimento.getPesoPorcao()).thenReturn(BigDecimal.valueOf(50));
    }

    @Test
    @DisplayName("Deve cadastrar um alimento em uma refeição com sucesso aplicando conversões")
    void cadastrar_Cenario1() {
        // Arrange
        // Qtd informada = 100g. Como o alimento tem peso base 50g, 100/50 = multiplicador 2x.
        var dadosCadastro = new DadosCadastroRefeicaoAlimento(
                idRefeicao, 
                idAlimento, 
                BigDecimal.valueOf(100), 
                "g"
        );

        when(refeicaoRepository.getReferenceById(idRefeicao)).thenReturn(refeicao);
        when(alimentoRepository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act
        DadosDetalhamentoRefeicaoAlimento resultado = service.cadastrar(dadosCadastro, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        // Calorias 100 * 2 = 200 / Proteína 10 * 2 = 20
        assertEquals(new BigDecimal("200.00"), resultado.caloriasCalculadas());
        assertEquals(new BigDecimal("20.00"), resultado.proteinaCalculada());
        verify(repository, times(1)).save(any(RefeicaoAlimento.class));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar manipular refeição de outro usuário")
    void cadastrar_Cenario2() {
        // Arrange (refaz mockup para simular ser de outro dono)
        when(refeicao.getUsuario()).thenReturn(outroUsuario);

        var dadosCadastro = new DadosCadastroRefeicaoAlimento(
                idRefeicao, idAlimento, BigDecimal.valueOf(100), "g"
        );

        when(refeicaoRepository.getReferenceById(idRefeicao)).thenReturn(refeicao);
        when(alimentoRepository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.cadastrar(dadosCadastro, usuarioLogado));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve detalhar refeição alimento contendo cálculo matemático correto")
    void detalhar_Cenario1() {
        // Arrange
        var idComposto = new RefeicaoAlimento.idRefeicaoAlimento(idRefeicao, idAlimento);
        var refeicaoAlimentoMock = mock(RefeicaoAlimento.class);

        when(refeicaoAlimentoMock.getRefeicao()).thenReturn(refeicao);
        when(refeicaoAlimentoMock.getAlimento()).thenReturn(alimento);
        when(refeicaoAlimentoMock.getQuantidade()).thenReturn(BigDecimal.valueOf(25)); // metade da porção padrão (50)
        when(refeicaoAlimentoMock.getUnidadeMedida()).thenReturn("g");

        when(repository.getReferenceById(idComposto)).thenReturn(refeicaoAlimentoMock);

        // Act
        DadosDetalhamentoRefeicaoAlimento resultado = service.detalhar(idRefeicao, idAlimento, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        // Calorias 100 * 0.5 = 50 / Carboidrato 5 * 0.5 = 2.5
        assertEquals(new BigDecimal("50.00"), resultado.caloriasCalculadas());
        assertEquals(new BigDecimal("2.50"), resultado.carboidratoCalculado());
    }

    @Test
    @DisplayName("Deve deletar correlação com sucesso")
    void deletar_Cenario1() {
        // Arrange
        var idComposto = new RefeicaoAlimento.idRefeicaoAlimento(idRefeicao, idAlimento);
        var refeicaoAlimentoMock = mock(RefeicaoAlimento.class);

        when(refeicaoAlimentoMock.getRefeicao()).thenReturn(refeicao);
        when(refeicaoAlimentoMock.getAlimento()).thenReturn(alimento);

        when(repository.getReferenceById(idComposto)).thenReturn(refeicaoAlimentoMock);

        // Act
        assertDoesNotThrow(() -> service.deletar(idRefeicao, idAlimento, usuarioLogado));

        // Assert
        verify(repository, times(1)).delete(refeicaoAlimentoMock);
    }
}
