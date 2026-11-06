package cl.zaror.alertas.domain

/** Reglas de quién puede hacer qué con las alertas. Sin dependencias de Android. */
object Permisos {

    const val ESTADO_ACTIVA = "Activa"
    const val ESTADO_ATENDIDA = "Atendida"
    const val ESTADO_CANCELADA = "Cancelada"

    /** El administrador ve todas las alertas; el vecino solo las suyas. */
    fun puedeVerTodas(rol: Rol): Boolean = rol == Rol.ADMINISTRADOR

    /** Solo el administrador valida y marca una alerta como atendida. */
    fun puedeMarcarAtendida(rol: Rol): Boolean = rol == Rol.ADMINISTRADOR

    /** Solo el administrador elimina alertas (para no perder evidencia). */
    fun puedeEliminar(rol: Rol): Boolean = rol == Rol.ADMINISTRADOR

    /** El vecino puede cancelar su propia alerta mientras siga activa. */
    fun puedeCancelar(
        rol: Rol,
        usuarioId: Int,
        duenoId: Int,
        estado: String
    ): Boolean {
        return rol == Rol.VECINO && usuarioId == duenoId && estado == ESTADO_ACTIVA
    }
}
