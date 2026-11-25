package cl.duoc.mssolicitudes.service;

import cl.duoc.mssolicitudes.dto.SolicitudRequest;
import cl.duoc.mssolicitudes.exception.RecursoNoEncontradoException;
import cl.duoc.mssolicitudes.exception.ReglaNegocioException;
import cl.duoc.mssolicitudes.model.EstadoSolicitud;
import cl.duoc.mssolicitudes.model.Solicitud;
import cl.duoc.mssolicitudes.repository.SolicitudRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Reglas del negocio de las solicitudes:
 *  - el código SOL-XXXXXXXX y la fecha los genera el servidor,
 *  - toda solicitud nace con estado "Recibida",
 *  - el servicio relacionado es obligatorio salvo en "Consulta" y "Otro requerimiento",
 *  - solo se aceptan los estados definidos en EstadoSolicitud.
 */
@Service
public class SolicitudService {

    static final Set<String> CATEGORIAS_SIN_SERVICIO = Set.of("Consulta", "Otro requerimiento");

    private final SolicitudRepository repository;

    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    public List<Solicitud> listar(String correoCliente) {
        if (correoCliente == null || correoCliente.isBlank()) {
            return repository.findAllByOrderByFechaCreacionDesc();
        }
        return repository.findByCorreoClienteIgnoreCaseOrderByFechaCreacionDesc(correoCliente.trim());
    }

    public Solicitud buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud con id " + id));
    }

    public Solicitud buscarPorCodigo(String codigo) {
        return repository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud " + codigo));
    }

    public Solicitud crear(SolicitudRequest request) {
        validarServicio(request);
        long ahora = System.currentTimeMillis();
        Solicitud nueva = new Solicitud();
        copiarDatos(request, nueva);
        nueva.setCodigo(generarCodigoUnico());
        nueva.setEstado(EstadoSolicitud.RECIBIDA);
        nueva.setCorreoCliente(request.correoCliente());
        nueva.setFechaCreacion(ahora);
        nueva.setFechaActualizacion(ahora);
        return repository.save(nueva);
    }

    public Solicitud actualizar(Long id, SolicitudRequest request) {
        validarServicio(request);
        Solicitud existente = buscarPorId(id);
        copiarDatos(request, existente);       // código, estado y fecha de creación NO cambian
        existente.setFechaActualizacion(System.currentTimeMillis());
        return repository.save(existente);
    }

    public Solicitud cambiarEstado(Long id, String estado) {
        if (!EstadoSolicitud.TODOS.contains(estado)) {
            throw new ReglaNegocioException("Estado no válido. Use uno de: " + EstadoSolicitud.TODOS);
        }
        Solicitud existente = buscarPorId(id);
        existente.setEstado(estado);
        existente.setFechaActualizacion(System.currentTimeMillis());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe la solicitud con id " + id);
        }
        repository.deleteById(id);
    }

    /** Identificador de seguimiento, por ejemplo SOL-3F9A1C2B (se reintenta si ya existe). */
    String generarCodigoUnico() {
        String codigo;
        do {
            codigo = "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.existsByCodigo(codigo));
        return codigo;
    }

    private void validarServicio(SolicitudRequest request) {
        boolean obligatorio = !CATEGORIAS_SIN_SERVICIO.contains(request.categoria());
        if (obligatorio && (request.servicio() == null || request.servicio().isBlank())) {
            throw new ReglaNegocioException("Indica el servicio o sitio relacionado");
        }
    }

    private void copiarDatos(SolicitudRequest origen, Solicitud destino) {
        destino.setTitulo(origen.titulo().trim());
        destino.setDescripcion(origen.descripcion().trim());
        destino.setCategoria(origen.categoria());
        destino.setServicio(origen.servicio() == null ? "" : origen.servicio().trim());
        destino.setEvidenciaUri(origen.evidenciaUri());
    }
}
