package com.welltrack.service.alimento;

import com.welltrack.domain.alimento.Alimento;
import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.alimento.DadosCadastroAlimento;
import com.welltrack.repository.alimento.AlimentoRepository;
import com.welltrack.repository.usuario.UsuarioRepository;
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

    @Test
    @DisplayName("Deve cadastrar um alimento com sucesso e retornar a instância salva")
    void cadastrar_deveCadastrarComSucesso() {
        // Arrange
        UUID idUsuarioLogado = UUID.randomUUID();
        UUID idAlimento = UUID.randomUUID();

        Usuario usuarioLogado = mock(Usuario.class);
        Alimento alimentoSalvo = mock(Alimento.class);

        when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);
        when(alimentoSalvo.getIdAlimento()).thenReturn(idAlimento);

        DadosCadastroAlimento dadosCadastro = new DadosCadastroAlimento(
                "Feijao",
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(5),
                BigDecimal.ZERO,
                "g",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(80),
                null,
                idUsuarioLogado
        );

        when(usuarioRepository.getReferenceById(idUsuarioLogado)).thenReturn(usuarioLogado);
        when(repository.save(any(Alimento.class))).thenReturn(alimentoSalvo);

        // Act
        Alimento resultado = service.cadastrar(dadosCadastro, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idAlimento, resultado.getIdAlimento());
        verify(usuarioRepository, times(1)).getReferenceById(idUsuarioLogado);
        verify(repository, times(1)).save(any(Alimento.class));
    }

    @Test
    @DisplayName("Não deve cadastrar alimento para outro usuário")
    void cadastrar_deveLancarAccessDeniedQuandoUsuarioForDiferenteDoLogado() {
        // Arrange
        UUID idUsuarioLogado = UUID.randomUUID();
        UUID idOutroUsuario = UUID.randomUUID();

        Usuario usuarioLogado = mock(Usuario.class);
        when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);

        DadosCadastroAlimento dadosCadastro = new DadosCadastroAlimento(
                "Maca",
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(1),
                BigDecimal.ZERO,
                "unidade",
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(50),
                null,
                idOutroUsuario
        );

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> service.cadastrar(dadosCadastro, usuarioLogado)
        );

        assertNotNull(exception);
        verify(usuarioRepository, never()).getReferenceById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve detalhar alimento corretamente quando pertencer ao usuário logado")
    void detalhar_deveRetornarAlimentoQuandoPertencerAoUsuarioLogado() {
        // Arrange
        UUID idUsuarioLogado = UUID.randomUUID();
        UUID idAlimento = UUID.randomUUID();

        Usuario usuarioLogado = mock(Usuario.class);
        Alimento alimento = mock(Alimento.class);

        when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);
        when(alimento.getIdAlimento()).thenReturn(idAlimento);
        when(alimento.getUsuario()).thenReturn(usuarioLogado);
        when(repository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act
        Alimento resultado = service.detalhar(idAlimento, usuarioLogado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idAlimento, resultado.getIdAlimento());
        verify(repository, times(1)).getReferenceById(idAlimento);
    }

    @Test
    @DisplayName("Não deve detalhar alimento de outro usuário")
    void detalhar_deveLancarAccessDeniedQuandoAlimentoForDeOutroUsuario() {
        // Arrange
        UUID idUsuarioLogado = UUID.randomUUID();
        UUID idOutroUsuario = UUID.randomUUID();
        UUID idAlimento = UUID.randomUUID();

        Usuario usuarioLogado = mock(Usuario.class);
        Usuario outroUsuario = mock(Usuario.class);
        Alimento alimento = mock(Alimento.class);

        when(usuarioLogado.getIdUsuario()).thenReturn(idUsuarioLogado);
        when(outroUsuario.getIdUsuario()).thenReturn(idOutroUsuario);
        when(alimento.getUsuario()).thenReturn(outroUsuario);
        when(repository.getReferenceById(idAlimento)).thenReturn(alimento);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> service.detalhar(idAlimento, usuarioLogado)
        );

        assertEquals(
                "Acesso negado: O recurso solicitado pertence a outro usuário.",
                exception.getMessage()
        );

        verify(repository, times(1)).getReferenceById(idAlimento);
    }
}