package cl.duoc.gestionsolicitudes.data.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.IOException
import retrofit2.Response

const val SIN_CONEXION_MICROSERVICIO =
    "No se pudo conectar con el servidor. Verifica que los microservicios estén en ejecución."

/** Error de red o del servidor, con un mensaje listo para mostrar en pantalla. */
class ApiException(val codigo: Int, mensaje: String) : Exception(mensaje)

/**
 * Ejecuta una llamada Retrofit y la convierte en Result:
 *  - 2xx            -> Result.success(cuerpo)
 *  - 4xx / 5xx      -> Result.failure con el "mensaje" que envía el microservicio
 *  - sin conexión   -> Result.failure con un mensaje claro
 */
suspend fun <T> llamarApi(
    mensajeSinConexion: String = SIN_CONEXION_MICROSERVICIO,
    peticion: suspend () -> Response<T>
): Result<T> {
    return try {
        val respuesta = peticion()
        if (respuesta.isSuccessful) {
            @Suppress("UNCHECKED_CAST")
            Result.success(respuesta.body() ?: Unit as T) // 204 No Content no trae cuerpo
        } else {
            Result.failure(ApiException(respuesta.code(), leerMensajeError(respuesta)))
        }
    } catch (e: IOException) {
        Result.failure(ApiException(0, mensajeSinConexion))
    }
}

private fun leerMensajeError(respuesta: Response<*>): String {
    val texto = respuesta.errorBody()?.string().orEmpty()
    return try {
        Gson().fromJson(texto, JsonObject::class.java)?.get("mensaje")?.asString
            ?: "Error ${respuesta.code()}"
    } catch (e: Exception) {
        "Error ${respuesta.code()}"
    }
}
