package cl.duoc.mssolicitudes.repository;

import cl.duoc.mssolicitudes.model.Solicitud;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA: los métodos se traducen solos a consultas SQL. */
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findAllByOrderByFechaCreacionDesc();

    List<Solicitud> findByCorreoClienteIgnoreCaseOrderByFechaCreacionDesc(String correoCliente);

    Optional<Solicitud> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigo(String codigo);
}
