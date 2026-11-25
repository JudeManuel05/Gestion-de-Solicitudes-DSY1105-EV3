package cl.duoc.gestionsolicitudes.data.remote.api

import cl.duoc.gestionsolicitudes.data.remote.dto.LoginRequest
import cl.duoc.gestionsolicitudes.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Endpoints del microservicio ms-usuarios (http://10.0.2.2:8081/). */
interface UsuarioApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<UsuarioDto>
}
