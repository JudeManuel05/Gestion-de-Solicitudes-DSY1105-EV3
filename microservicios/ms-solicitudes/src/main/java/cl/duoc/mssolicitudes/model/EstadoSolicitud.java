package cl.duoc.mssolicitudes.model;

import java.util.List;

/** Estados válidos del ciclo de vida de una solicitud. */
public final class EstadoSolicitud {

    public static final String RECIBIDA = "Recibida";
    public static final String EN_REVISION = "En revisión";
    public static final String EN_PROCESO = "En proceso";
    public static final String RESUELTA = "Resuelta";
    public static final String CERRADA = "Cerrada";

    public static final List<String> TODOS = List.of(RECIBIDA, EN_REVISION, EN_PROCESO, RESUELTA, CERRADA);

    private EstadoSolicitud() {
    }
}
