package cl.duoc.gestionsolicitudes.data.repository

import cl.duoc.gestionsolicitudes.data.remote.api.UsuarioApi
import cl.duoc.gestionsolicitudes.data.remote.dto.LoginRequest
import cl.duoc.gestionsolicitudes.data.remote.dto.UsuarioDto
import cl.duoc.gestionsolicitudes.data.remote.llamarApi

/**
 * Repository de usuarios: el ViewModel no sabe que existe Retrofit,
 * solo pide "autenticar" y recibe un Result (éxito o error).
 */
class UsuarioRepository(private val api: UsuarioApi) {

    suspend fun autenticar(correo: String, clave: String): Result<UsuarioDto> =
        llamarApi { api.login(LoginRequest(correo.trim(), clave)) }
}
