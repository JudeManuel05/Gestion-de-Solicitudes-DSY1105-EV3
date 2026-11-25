package cl.duoc.gestionsolicitudes.data.repository

import cl.duoc.gestionsolicitudes.data.remote.api.IndicadoresApi
import cl.duoc.gestionsolicitudes.data.remote.llamarApi
import cl.duoc.gestionsolicitudes.model.Indicador

/**
 * Consume la API EXTERNA mindicador.cl y transforma su JSON
 * en una lista simple de indicadores para la pantalla.
 */
class IndicadoresRepository(private val api: IndicadoresApi) {

    suspend fun obtenerIndicadores(): Result<Pair<String, List<Indicador>>> =
        llamarApi("No se pudo consultar mindicador.cl. Revisa la conexión a Internet e intenta de nuevo.") {
            api.obtenerIndicadores()
        }.map { respuesta ->
            val lista = listOfNotNull(respuesta.uf, respuesta.dolar, respuesta.euro, respuesta.utm)
                .map { Indicador(it.codigo, it.nombre, it.valor, it.unidad_medida) }
            (respuesta.fecha ?: "") to lista
        }
}
