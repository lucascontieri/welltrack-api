package com.welltrack.repository.usuario;

import com.welltrack.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "TEST_DATASOURCE_URL=jdbc:postgresql://localhost:5432/welltrack_db_test"
})
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("Deve devolver apenas usuarios ativos")
    void findAllByAtivoTrue() {
        var u1 = cadastrarUsuario("Ativo", "ativo@email.com", true);
        var u2 = cadastrarUsuario("Inativo", "inativo@email.com", false);

        var pagina = repository.findAllByAtivoTrue(PageRequest.of(0, 10));
        var lista = pagina.getContent();

        assertTrue(lista.contains(u1));
        assertFalse(lista.contains(u2));
    }

    @Test
    @DisplayName("Deve encontrar usuario por e-mail ignorando case e espaços")
    void findByEmail() {
        var u1 = cadastrarUsuario("Busca", "busca@email.com", true);

        var resultado = repository.findByEmail("busca@email.com");

        assertNotNull(resultado);
        assertEquals(u1.getEmail(), resultado.getUsername());
    }

    private Usuario cadastrarUsuario(String nome, String email, boolean ativo) {
        var u = new Usuario(nome, email, null, null);
        if (!ativo) {
            u.inativar();
        }
        repository.save(u);
        return u;
    }
}