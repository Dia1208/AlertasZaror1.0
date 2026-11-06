package cl.zaror.alertas.domain

/**
 * Administradores fijos de la app. No se editan desde la interfaz:
 * para cambiarlos hay que modificar esta lista y publicar una versión nueva.
 *
 * Solución provisional: cuando exista el backend, el rol vendrá del servidor.
 * Mientras tanto la lista vive en el cliente, así que NO es una barrera
 * de seguridad real, solo ordena los permisos dentro de la app.
 */
object AdminsFijos {

    /** Teléfonos (9 dígitos, ej. "912345678") de los administradores. */
    val TELEFONOS: Set<String> = setOf(
        // "9XXXXXXXX", // súper administrador
    )

    fun rolPara(telefono: String, admins: Set<String> = TELEFONOS): Rol {
        return if (telefono.trim() in admins) Rol.ADMINISTRADOR else Rol.VECINO
    }
}
