package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.exception.CorreoDuplicadoException;
import cl.duoc.msusuarios.exception.CredencialesInvalidasException;
import cl.duoc.msusuarios.exception.RecursoNoEncontradoException;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Capa de servicio: aquí viven las REGLAS DEL NEGOCIO.
 * El controlador solo recibe la petición; el repositorio solo guarda/lee.
 */
@Service
public class UsuarioService {

    public static final String ROL_CLIENTE = "CLIENTE";

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias por constructor (facilita las pruebas con Mockito)
    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = repository.findByCorreoIgnoreCase(request.correo().trim())
                .orElseThrow(CredencialesInvalidasException::new);
        // matches() compara la clave escrita con el hash guardado
        if (!passwordEncoder.matches(request.clave(), usuario.getClave())) {
            throw new CredencialesInvalidasException();
        }
        return UsuarioResponse.desde(usuario);
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String correo = request.correo().trim().toLowerCase();
        if (repository.existsByCorreoIgnoreCase(correo)) {
            throw new CorreoDuplicadoException(correo);
        }
        Usuario nuevo = new Usuario(null, request.nombre().trim(), correo,
                passwordEncoder.encode(request.clave()), ROL_CLIENTE);
        return UsuarioResponse.desde(repository.save(nuevo));
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(UsuarioResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }
}
