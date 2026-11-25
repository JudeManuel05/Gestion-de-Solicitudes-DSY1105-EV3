package cl.duoc.gestionsolicitudes.data.remote.dto

/** JSON enviado al crear (POST) o editar (PUT) una solicitud. */
data class SolicitudRequest(
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String,
    val evidenciaUri: String?,
    val correoCliente: String?
)

/** JSON enviado a PATCH api/solicitudes/{id}/estado */
data class EstadoRequest(val estado: String)
