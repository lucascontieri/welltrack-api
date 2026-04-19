package com.welltrack.service.registrotreino;

import com.welltrack.domain.registrotreino.RegistroTreino;
import com.welltrack.domain.treino.Treino;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.registrotreino.DadosCadastroRegistroTreino;
import com.welltrack.repository.registrotreino.RegistroTreinoRepository;
import com.welltrack.repository.treino.TreinoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
import com.welltrack.service.registrotreino.RegistroTreinoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroTreinoServiceTest {

    @InjectMocks
    private RegistroTreinoService service;

    @Mock
    private RegistroTreinoRepository repository;

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioLogado;
    private Usuario outroUsuario;
    private Treino treinoMock;
    private RegistroTreino registroTreinoMock;

    private UUID idUsuarioLogado = UUID.randomUUID();
    private UUID idOutroUsuario = UUID.randomUUID();
    private UUID idTreino = UUID.randomUUID();
    private UUID idRegistro = UUID.randomUUID();

    @BeforeEach
    void setup() {
        usuarioLogado = mock(Usuario.class);
        lenient().when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);

        outroUsuario = mock(Usuario.class);
        lenient().when(outroUsuario.getIdUsuario()).thenReturn(idOutroUsuario);

        treinoMock = mock(Treino.class);
        lenient().when(treinoMock.getIdTreino()).thenReturn(idTreino);
        lenient().when(treinoMock.getUsuario()).thenReturn(usuarioLogado);

        registroTreinoMock = mock(RegistroTreino.class);
        lenient().when(registroTreinoMock.getIdRegistro()).thenReturn(idRegistro);
        lenient().when(registroTreinoMock.getUsuario()).thenReturn(usuarioLogado);
    }

    @Test
    @DisplayName("Deve cadastrar um registro de treino com sucesso e salvar no banco")
    void cadastrar_Cenario1() {
        // Arrange
        var dadosCadastro = new DadosCadastroRegistroTreino(
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                true,
                idTreino,
                idUsuarioLogado
        );

        when(treinoRepository.getReferenceById(idTreino)).thenReturn(treinoMock);
        when(usuarioRepository.getReferenceById(idUsuarioLogado)).thenReturn(usuarioLogado);
        when(repository.save(any(RegistroTreino.class))).thenReturn(registroTreinoMock);

        // Act
        RegistroTreino resultado = service.cadastrar(dadosCadastro, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        verify(repository, times(1)).save(any(RegistroTreino.class));
    }

    @Test
    @DisplayName("Nao deve detalhar historico de treino pertencente a outro usuario (Proteção IDOR)")
    void detalhar_CenarioIdor() {
        // Arrange - Simulando que o registro alvo seja do "outro usuario"
        when(registroTreinoMock.getUsuario()).thenReturn(outroUsuario);
        when(repository.getReferenceById(idRegistro)).thenReturn(registroTreinoMock);

        // Act & Assert
        assertThrows(AccessDeniedException.class, 
                () -> service.detalhar(idRegistro, usuarioLogado));
        
        verify(repository, times(1)).getReferenceById(idRegistro);
    }

    @Test
    @DisplayName("Deve detalhar historico de treino corretamente quando for do mesmo usuario logado")
    void detalhar_Cenario1() {
        // Arrange 
        when(repository.getReferenceById(idRegistro)).thenReturn(registroTreinoMock);

        // Act
        RegistroTreino resultado = service.detalhar(idRegistro, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idRegistro, resultado.getIdRegistro());
    }
}
