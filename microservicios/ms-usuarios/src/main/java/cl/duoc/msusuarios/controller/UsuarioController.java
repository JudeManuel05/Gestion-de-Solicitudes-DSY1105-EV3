package cl.duoc.msusuarios.controller;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST del microservicio de usuarios.
 * Base: http://localhost:8081/api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST /api/usuarios/login -> 200 con el usuario, 401 si las credenciales no coinciden
    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    // POST /api/usuarios/registro -> 201 con el usuario creado, 409 si el correo ya existe
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return service.registrar(request);
    }

    // GET /api/usuarios -> lista de usuarios (sin contraseñas)
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
