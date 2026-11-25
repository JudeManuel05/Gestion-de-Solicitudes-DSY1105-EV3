package cl.duoc.msusuarios.dto;

import cl.duoc.msusuarios.model.Usuario;

/** Lo que se devuelve a la app: nunca incluye la contraseña. */
public record UsuarioResponse(Long id, String nombre, String correo, String rol) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
