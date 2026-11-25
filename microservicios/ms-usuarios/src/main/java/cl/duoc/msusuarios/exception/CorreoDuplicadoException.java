package cl.duoc.msusuarios.exception;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String correo) {
        super("Ya existe un usuario con el correo " + correo);
    }
}
