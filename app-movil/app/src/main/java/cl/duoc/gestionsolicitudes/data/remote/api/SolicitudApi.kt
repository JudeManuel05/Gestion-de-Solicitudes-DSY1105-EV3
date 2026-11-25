package cl.duoc.gestionsolicitudes.data.remote.api

import cl.duoc.gestionsolicitudes.data.remote.dto.EstadoRequest
import cl.duoc.gestionsolicitudes.data.remote.dto.SolicitudRequest
import cl.duoc.gestionsolicitudes.model.Solicitud
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Endpoints del microservicio ms-solicitudes (http://10.0.2.2:8082/).
 * Cada función equivale a un endpoint del SolicitudController de Spring Boot.
 */
interface SolicitudApi {

    @GET("api/solicitudes")
    suspend fun listar(): Response<List<Solicitud>>

    @GET("api/solicitudes/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Solicitud>

    @GET("api/solicitudes/codigo/{codigo}")
    suspend fun obtenerPorCodigo(@Path("codigo") codigo: String): Response<Solicitud>

    @POST("api/solicitudes")
    suspend fun crear(@Body request: SolicitudRequest): Response<Solicitud>

    @PUT("api/solicitudes/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body request: SolicitudRequest): Response<Solicitud>

    @PATCH("api/solicitudes/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body request: EstadoRequest): Response<Solicitud>

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
