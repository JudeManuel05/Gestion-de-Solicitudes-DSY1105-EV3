package cl.duoc.gestionsolicitudes.data.repository

import cl.duoc.gestionsolicitudes.data.remote.api.SolicitudApi
import cl.duoc.gestionsolicitudes.data.remote.dto.EstadoRequest
import cl.duoc.gestionsolicitudes.data.remote.dto.SolicitudRequest
import cl.duoc.gestionsolicitudes.data.remote.llamarApi
import cl.duoc.gestionsolicitudes.model.Solicitud

/**
 * Repository: única puerta de acceso a las solicitudes.
 * PERSISTENCIA REMOTA: cada operación viaja por HTTP al microservicio ms-solicitudes,
 * que la guarda en su base de datos con JPA.
 */
class SolicitudRepository(private val api: SolicitudApi) {

    suspend fun listar(): Result<List<Solicitud>> = llamarApi { api.listar() }

    suspend fun obtenerPorId(id: Long): Result<Solicitud> = llamarApi { api.obtenerPorId(id) }

    suspend fun obtenerPorCodigo(codigo: String): Result<Solicitud> =
        llamarApi { api.obtenerPorCodigo(codigo.trim()) }

    suspend fun crear(request: SolicitudRequest): Result<Solicitud> = llamarApi { api.crear(request) }

    suspend fun actualizar(id: Long, request: SolicitudRequest): Result<Solicitud> =
        llamarApi { api.actualizar(id, request) }

    suspend fun cambiarEstado(id: Long, estado: String): Result<Solicitud> =
        llamarApi { api.cambiarEstado(id, EstadoRequest(estado)) }

    suspend fun eliminar(id: Long): Result<Unit> = llamarApi { api.eliminar(id) }
}
