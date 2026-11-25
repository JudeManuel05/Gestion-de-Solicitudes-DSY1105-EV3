package cl.duoc.msusuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.exception.CorreoDuplicadoException;
import cl.duoc.msusuarios.exception.CredencialesInvalidasException;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Prueba la lógica del servicio SIN base de datos:
 * el repositorio y el cifrador se reemplazan por mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;      // mock: no toca la BD

    @Mock
    private PasswordEncoder passwordEncoder;   // mock: controlamos si la clave coincide

    @InjectMocks
    private UsuarioService service;            // clase real que se prueba

    private final Usuario cliente =
            new Usuario(1L, "Cliente Demo", "cliente@demo.cl", "HASH", "CLIENTE");

    @Test
    void loginCorrectoDevuelveElUsuarioSinClave() {
        // Arrange (preparar): el repositorio encuentra al usuario y la clave coincide
        when(repository.findByCorreoIgnoreCase("cliente@demo.cl")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("demo123", "HASH")).thenReturn(true);

        // Act (ejecutar)
        UsuarioResponse respuesta = service.login(new LoginRequest("cliente@demo.cl", "demo123"));

        // Assert (verificar)
        assertEquals("Cliente Demo", respuesta.nombre());
        assertEquals("CLIENTE", respuesta.rol());
    }

    @Test
    void loginConClaveIncorrectaLanzaExcepcion() {
        when(repository.findByCorreoIgnoreCase("cliente@demo.cl")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("malaclave", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("cliente@demo.cl", "malaclave")));
    }

    @Test
    void loginConCorreoInexistenteLanzaExcepcion() {
        when(repository.findByCorreoIgnoreCase("nadie@demo.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("nadie@demo.cl", "demo123")));
    }

    @Test
    void registrarCifraLaClaveYAsignaRolCliente() {
        when(repository.existsByCorreoIgnoreCase("nuevo@demo.cl")).thenReturn(false);
        when(passwordEncoder.encode("secreto1")).thenReturn("HASH-NUEVO");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UsuarioResponse creado = service.registrar(new RegistroRequest("Nuevo", "Nuevo@Demo.cl", "secreto1"));

        assertEquals(10L, creado.id());
        assertEquals("nuevo@demo.cl", creado.correo()); // se normaliza a minúsculas
        assertEquals("CLIENTE", creado.rol());
        verify(passwordEncoder).encode("secreto1");     // la clave SIEMPRE se cifra
    }

    @Test
    void registrarCorreoDuplicadoNoGuarda() {
        when(repository.existsByCorreoIgnoreCase("cliente@demo.cl")).thenReturn(true);

        assertThrows(CorreoDuplicadoException.class,
                () -> service.registrar(new RegistroRequest("Otro", "cliente@demo.cl", "demo123")));
        verify(repository, never()).save(any());
    }
}
