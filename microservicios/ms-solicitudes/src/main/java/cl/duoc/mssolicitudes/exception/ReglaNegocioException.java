package cl.duoc.mssolicitudes.exception;

/** Se lanza cuando los datos tienen formato válido pero rompen una regla del negocio. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
