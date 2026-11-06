package cl.zaror.alertas.backend.auth;

/** Datos del usuario que se pueden mostrar. Nunca incluye la contraseña ni su hash. */
public record UsuarioResponse(long id, String nombre, String telefono, String rol, String estado) {

    static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.id(), usuario.nombre(), usuario.telefono(), usuario.rol(), usuario.estado());
    }
}
