package cl.duoc.gestionsolicitudes.data.remote.dto

/** JSON que responde ms-usuarios (nunca trae la contraseña). */
data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String
)
