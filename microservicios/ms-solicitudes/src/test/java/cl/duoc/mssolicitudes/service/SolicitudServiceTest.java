package cl.duoc.mssolicitudes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.mssolicitudes.dto.SolicitudRequest;
import cl.duoc.mssolicitudes.exception.RecursoNoEncontradoException;
import cl.duoc.mssolicitudes.exception.ReglaNegocioException;
import cl.duoc.mssolicitudes.model.EstadoSolicitud;
import cl.duoc.mssolicitudes.model.Solicitud;
import cl.duoc.mssolicitudes.repository.SolicitudRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de las reglas del negocio.
 * El repositorio JPA se reemplaza por un mock: no se usa base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository repository;

    @InjectMocks
    private SolicitudService service;

    private SolicitudRequest requestValido() {
        return new SolicitudRequest("Sitio web caído", "El sitio no carga desde ayer en la mañana",
                "Incidente técnico", "www.cliente.cl", null, "cliente@demo.cl");
    }

    @Test
    void crearAsignaCodigoEstadoRecibidaYFechas() {
        when(repository.existsByCodigo(anyString())).thenReturn(false);
        // El mock devuelve la misma entidad que recibe (como haría la BD)
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Solicitud creada = service.crear(requestValido());

        assertTrue(creada.getCodigo().matches("SOL-[0-9A-F]{8}"));
        assertEquals(EstadoSolicitud.RECIBIDA, creada.getEstado());
        assertNotNull(creada.getFechaCreacion());
        assertEquals("cliente@demo.cl", creada.getCorreoCliente());
    }

    @Test
    void crearSinServicioEnCategoriaQueLoExigeLanzaError() {
        SolicitudRequest sinServicio = new SolicitudRequest("Sitio web caído",
                "El sitio no carga desde ayer en la mañana", "Hosting", " ", null, null);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.crear(sinServicio));
        assertEquals("Indica el servicio o sitio relacionado", error.getMessage());
        verify(repository, never()).save(any());   // nunca llega a la BD
    }

    @Test
    void crearConsultaSinServicioEsValido() {
        when(repository.existsByCodigo(anyString())).thenReturn(false);
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));
        SolicitudRequest consulta = new SolicitudRequest("Consulta de precios",
                "Quiero saber el valor de un plan de hosting", "Consulta", null, null, null);

        Solicitud creada = service.crear(consulta);

        assertEquals("", creada.getServicio());
    }

    @Test
    void cambiarEstadoValidoActualizaLaSolicitud() {
        Solicitud existente = new Solicitud();
        existente.setId(1L);
        existente.setEstado(EstadoSolicitud.RECIBIDA);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Solicitud actualizada = service.cambiarEstado(1L, EstadoSolicitud.EN_PROCESO);

        assertEquals(EstadoSolicitud.EN_PROCESO, actualizada.getEstado());
    }

    @Test
    void cambiarEstadoInvalidoLanzaError() {
        assertThrows(ReglaNegocioException.class, () -> service.cambiarEstado(1L, "Inventado"));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarInexistenteLanzaNoEncontrado() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void eliminarExistenteLlamaAlRepositorio() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
