package cl.duoc.msusuarios.repository;

import cl.duoc.msusuarios.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA genera el SQL a partir del nombre de los métodos.
 * findByCorreoIgnoreCase -> SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(?)
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);
}
