package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=faker",
        "GROQ_API_KEY=dumb_api_key"
})
@Transactional
class UsuarioUCIntegrationTest {

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