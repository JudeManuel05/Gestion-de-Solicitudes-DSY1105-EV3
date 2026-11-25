package cl.duoc.gestionsolicitudes.model

/** Indicador económico listo para mostrar (viene de la API externa mindicador.cl). */
data class Indicador(
    val codigo: String,
    val nombre: String,
    val valor: Double,
    val unidad: String
)

/** Estado de la pantalla de indicadores. */
data class IndicadoresUiState(
    val cargando: Boolean = false,
    val indicadores: List<Indicador> = emptyList(),
    val fecha: String = "",
    val error: String? = null
)
