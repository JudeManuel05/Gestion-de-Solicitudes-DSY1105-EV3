package cl.duoc.gestionsolicitudes.model

/** Estado de la lista de solicitudes que viene del microservicio. */
data class ListaUiState(
    val cargando: Boolean = false,
    val solicitudes: List<Solicitud> = emptyList(),
    val error: String? = null
)
