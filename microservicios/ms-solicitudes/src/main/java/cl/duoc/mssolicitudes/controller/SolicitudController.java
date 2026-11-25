package cl.duoc.mssolicitudes.controller;

import cl.duoc.mssolicitudes.dto.EstadoRequest;
import cl.duoc.mssolicitudes.dto.SolicitudRequest;
import cl.duoc.mssolicitudes.model.Solicitud;
import cl.duoc.mssolicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST del microservicio de solicitudes (CRUD completo).
 * Base: http://localhost:8082/api/solicitudes
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    // GET /api/solicitudes            -> todas
    // GET /api/solicitudes?correo=x   -> solo las del cliente x
    @GetMapping
    public List<Solicitud> listar(@RequestParam(required = false) String correo) {
        return service.listar(correo);
    }

    // GET /api/solicitudes/5
    @GetMapping("/{id}")
    public Solicitud buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // GET /api/solicitudes/codigo/SOL-1A2B3C4D  (lo usa el lector QR)
    @GetMapping("/codigo/{codigo}")
    public Solicitud buscarPorCodigo(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo);
    }

    // POST /api/solicitudes -> 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@Valid @RequestBody SolicitudRequest request) {
        return service.crear(request);
    }

    // PUT /api/solicitudes/5 -> reemplaza los datos editables
    @PutMapping("/{id}")
    public Solicitud actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudRequest request) {
        return service.actualizar(id, request);
    }

    // PATCH /api/solicitudes/5/estado -> cambia solo el estado
    @PatchMapping("/{id}/estado")
    public Solicitud cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return service.cambiarEstado(id, request.estado());
    }

    // DELETE /api/solicitudes/5 -> 204 No Content
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
