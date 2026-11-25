package cl.duoc.msusuarios.config;

import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Crea las cuentas ficticias de prueba la primera vez que se levanta el servicio. */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Usuario(null, "Cliente Demo", "cliente@demo.cl", encoder.encode("demo123"), "CLIENTE"));
                repository.save(new Usuario(null, "Coordinador Demo", "admin@demo.cl", encoder.encode("demo123"), "COORDINADOR"));
            }
        };
    }
}
