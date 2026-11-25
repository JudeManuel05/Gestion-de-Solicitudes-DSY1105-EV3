package cl.duoc.gestionsolicitudes.model

/**
 * Solicitud tal como la entrega el microservicio ms-solicitudes (JSON -> objeto).
 * Reemplaza a la antigua SolicitudEntity de Room: ahora la fuente de verdad es el servidor.
 */
data class Solicitud(
    val id: Long,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String?,
    val evidenciaUri: String?,
    val estado: String,
    val correoCliente: String?,
    val fechaCreacion: Long,
    val fechaActualizacion: Long
)
