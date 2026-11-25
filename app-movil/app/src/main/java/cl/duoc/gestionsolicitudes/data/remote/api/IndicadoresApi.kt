package cl.duoc.gestionsolicitudes.data.remote.api

import cl.duoc.gestionsolicitudes.data.remote.dto.IndicadoresResponse
import retrofit2.Response
import retrofit2.http.GET

/** API EXTERNA (no desarrollada por nosotros): https://mindicador.cl/api */
interface IndicadoresApi {

    @GET("api")
    suspend fun obtenerIndicadores(): Response<IndicadoresResponse>
}
