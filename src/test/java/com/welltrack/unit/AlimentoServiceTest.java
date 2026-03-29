package com.welltrack.unit;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.alimento.DadosCadastroAlimento;
import com.welltrack.repository.alimento.AlimentoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.service.alimento.AlimentoService;
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
class AlimentoServiceTest {

    @InjectMocks
    private AlimentoService service;

    @Mock
    private AlimentoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioLogado;
    private Usuario outroUsuario;
    private Alimento alimento;

    private UUID idUsuarioLogado = UUID.randomUUID();
    private UUID idOutroUsuario = UUID.randomUUID();
    private UUID idAlimento = UUID.randomUUID();

    @BeforeEach
    void setup() {
        usuarioLogado = mock(Usuario.class);
        lenient().when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);

        outroUsuario = mock(Usuario.class);
        lenient().when(outroUsuario.getIdUsuario()).thenReturn(idOutroUsuario);

        alimento = mock(Alimento.class);
        lenient().when(alimento.getIdAlimento()).thenReturn(idAlimento);
        lenient().when(alimento.getUsuario()).thenReturn(usuarioLogado);
    }

    @Test
    @DisplayName("Deve cadastrar um alimento com sucesso e retornar a instancia salva")
    void cadastrar_Cenario1() {
        // Arrange
        var dadosCadastro = new DadosCadastroAlimento(
                "Feijao", BigDecimal.valueOf(15), BigDecimal.valueOf(5), 
                BigDecimal.ZERO, "g", BigDecimal.valueOf(100), 
                BigDecimal.valueOf(80), null, idUsuarioLogado
        );

        when(usuarioRepository.getReferenceById(idUsuarioLogado)).thenReturn(usuarioLogado);
        when(repository.save(any(Alimento.class))).thenReturn(alimento);

        // Act
        Alimento resultado = service.cadastrar(dadosCadastro, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idAlimento, resultado.getIdAlimento());
        verify(repository, times(1)).save(any(Alimento.class));
    }

    @Test
    @DisplayName("Nao deve detalhar alimento de outro usuario lançando AccessDeniedException (IDOR)")
    void detalhar_CenarioIdor() {
        // Arrange
        // Simulando que o alimento no banco de dados pertence a outro usuario
        when(alimento.getUsuario()).thenReturn(outroUsuario);
        when(repository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
                () -> service.detalhar(idAlimento, usuarioLogado));

        assertEquals("Acesso negado: O recurso solicitado pertence a outro usuário.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve detalhar alimento corretamente quando pertencer ao dono real (usuário logado)")
    void detalhar_CenarioSucesso() {
        // Arrange
        when(repository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act
        Alimento resultado = service.detalhar(idAlimento, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idAlimento, resultado.getIdAlimento());
    }

    @Test
    @DisplayName("Deve lancar uma exception se tentar cadastrar atribuindo um recurso a um usuario alvo diferente do logado")
    void cadastrar_CenarioIdor() {
        // Arrange - Forçando a tentativa de criação para o ID do "outro usuário" mas usando o token do usuário logado
        var dadosCadastro = new DadosCadastroAlimento(
                "Maca", BigDecimal.valueOf(15), BigDecimal.valueOf(1), 
                BigDecimal.ZERO, "unidade", BigDecimal.valueOf(1), 
                BigDecimal.valueOf(50), null, idOutroUsuario
        );

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.cadastrar(dadosCadastro, usuarioLogado));
        
        // repository save never should be called
        verify(repository, never()).save(any());
    }
}
