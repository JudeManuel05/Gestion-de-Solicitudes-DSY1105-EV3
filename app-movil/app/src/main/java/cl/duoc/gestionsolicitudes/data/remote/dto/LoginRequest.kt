package cl.duoc.gestionsolicitudes.data.remote.dto

/** JSON enviado a POST api/usuarios/login */
data class LoginRequest(
    val correo: String,
    val clave: String
)
