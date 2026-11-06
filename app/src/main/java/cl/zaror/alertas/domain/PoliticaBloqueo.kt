package cl.zaror.alertas.domain

/** Bloqueo temporal del login tras varios intentos fallidos. Lógica pura, sin Android. */
object PoliticaBloqueo {

    const val MAX_INTENTOS = 5
    const val DURACION_MS = 5 * 60 * 1000L

    data class Estado(
        val intentosFallidos: Int,
        val bloqueadoHasta: Long
    )

    fun estaBloqueado(bloqueadoHasta: Long, ahora: Long): Boolean = ahora < bloqueadoHasta

    fun segundosRestantes(bloqueadoHasta: Long, ahora: Long): Long {
        val faltanMs = bloqueadoHasta - ahora
        return if (faltanMs <= 0) 0 else (faltanMs + 999) / 1000
    }

    /** Estado nuevo después de un intento fallido. Al llegar al máximo se bloquea y reinicia el conteo. */
    fun tras(intentosActuales: Int, ahora: Long): Estado {
        val nuevos = intentosActuales + 1
        return if (nuevos >= MAX_INTENTOS) {
            Estado(intentosFallidos = 0, bloqueadoHasta = ahora + DURACION_MS)
        } else {
            Estado(intentosFallidos = nuevos, bloqueadoHasta = 0L)
        }
    }
}
