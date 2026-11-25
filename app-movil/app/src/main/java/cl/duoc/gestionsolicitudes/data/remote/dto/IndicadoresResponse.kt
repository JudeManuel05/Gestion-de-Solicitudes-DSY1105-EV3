package cl.duoc.gestionsolicitudes.data.remote.dto

/**
 * Respuesta de https://mindicador.cl/api (solo los campos que usamos).
 * Gson asigna cada clave del JSON a la propiedad con el mismo nombre.
 */
data class IndicadoresResponse(
    val fecha: String?,
    val uf: IndicadorDto?,
    val dolar: IndicadorDto?,
    val euro: IndicadorDto?,
    val utm: IndicadorDto?
)

data class IndicadorDto(
    val codigo: String,
    val nombre: String,
    val unidad_medida: String,
    val fecha: String,
    val valor: Double
)
