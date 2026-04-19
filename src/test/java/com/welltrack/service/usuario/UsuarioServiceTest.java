package com.welltrack.service.usuario;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.usuario.DadosCadastroUsuario;
import com.welltrack.exception.ConflitoException;
import com.welltrack.repository.usuario.UsuarioRepository;

import com.welltrack.service.usuario.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioAcessoService usuarioAcessoService;

    @Test
    @DisplayName("Deve cadastrar usuario com e-mail normalizado, senha criptografada e envio de verificacao")
    void cadastrar_CenarioSucesso() {
        var dados = new DadosCadastroUsuario(
                "Lucas",
                null,
                LocalDate.of(1998, 5, 10),
                null,
                null,
                "  Lucas@Email.COM  ",
                "Senha123",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        when(repository.findOptionalByEmail("lucas@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Senha123")).thenReturn("senha-criptografada");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = service.cadastrar(dados);

        assertEquals("lucas@email.com", usuario.getEmail());
        assertEquals("senha-criptografada", usuario.getSenha());
        verify(usuarioAcessoService).iniciarVerificacaoEmail(usuario);
    }

    @Test
    @DisplayName("Nao deve cadastrar usuario com e-mail duplicado")
    void cadastrar_CenarioEmailDuplicado() {
        var dados = new DadosCadastroUsuario(
                "Lucas",
                null,
                LocalDate.of(1998, 5, 10),
                null,
                null,
                "Lucas@Email.COM",
                "Senha123",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        when(repository.findOptionalByEmail("lucas@email.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(ConflitoException.class, () -> service.cadastrar(dados));

        verify(repository, never()).save(any());
        verify(usuarioAcessoService, never()).iniciarVerificacaoEmail(any());
    }
}
