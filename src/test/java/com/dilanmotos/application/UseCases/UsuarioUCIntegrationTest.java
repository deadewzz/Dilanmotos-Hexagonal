package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.dilanmotos.domain.port.ChatPort;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345",
        "GOOGLE_API_KEY=dummy_google_key",
        "MAIL_USERNAME=test@dilanmotos.com",
        "MAIL_PASSWORD=dummy_password"
})
@Transactional
class UsuarioUCIntegrationTest {

    @MockBean
    private ChatPort chatPort; // Desactivamos llamadas externas a la IA

    @Autowired
    private UsuarioUC usuarioUC;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Debe registrar y consultar un usuario en la base de datos")
    void testRegistrarYConsultarUsuario() {
        Faker faker = new Faker(new Locale("es"));

        Usuario usuario = new Usuario();
        usuario.setNombre(faker.name().fullName());
        String correo = faker.internet().emailAddress();
        usuario.setCorreo(correo);
        usuario.setContrasena("Clave123*");

        Usuario guardado = usuarioUC.registrar(usuario);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdUsuario());

        Optional<Usuario> usuarioEnBD = usuarioRepository.buscarPorCorreo(correo);
        assertTrue(usuarioEnBD.isPresent());
    }
}