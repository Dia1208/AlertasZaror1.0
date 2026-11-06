package cl.zaror.alertas.data

import cl.zaror.alertas.domain.PoliticaBloqueo
import cl.zaror.alertas.domain.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Resultado de un intento de inicio de sesión. */
sealed interface ResultadoAuth {
    data class Exito(val usuario: UsuarioEntity) : ResultadoAuth

    /** Teléfono o clave incorrectos (no se distingue cuál, a propósito). */
    data object Invalidas : ResultadoAuth

    data class Bloqueado(val segundos: Long) : ResultadoAuth
}

class UsuariosRepository(
    private val usuarioDao: UsuarioDao
) {

    /** Registra un usuario nuevo. Devuelve su id, o null si el teléfono ya está registrado. */
    suspend fun registrar(
        nombre: String,
        telefono: String,
        direccion: String,
        clave: String
    ): Int? {
        val telefonoLimpio = telefono.trim()

        if (usuarioDao.buscarPorTelefono(telefonoLimpio) != null) {
            return null
        }

        // El hash es costoso a propósito: se calcula fuera del hilo principal.
        val sal = Seguridad.generarSal()
        val hash = withContext(Dispatchers.Default) { Seguridad.hashear(clave, sal) }

        val usuario = UsuarioEntity(
            nombre = nombre.trim(),
            telefono = telefonoLimpio,
            direccion = direccion.trim(),
            claveHash = hash,
            sal = sal,
            versionHash = Seguridad.VERSION_ACTUAL
        )

        return usuarioDao.insertar(usuario).toInt()
    }

    /**
     * Valida teléfono y clave. Cuenta los fallos y bloquea el login temporalmente al llegar al
     * máximo. Si la cuenta usa un hash antiguo, lo actualiza tras un inicio de sesión correcto.
     */
    suspend fun autenticar(
        telefono: String,
        clave: String,
        ahora: Long = System.currentTimeMillis()
    ): ResultadoAuth {
        val usuario = usuarioDao.buscarPorTelefono(telefono.trim())
            ?: return ResultadoAuth.Invalidas

        if (PoliticaBloqueo.estaBloqueado(usuario.bloqueadoHasta, ahora)) {
            return ResultadoAuth.Bloqueado(
                PoliticaBloqueo.segundosRestantes(usuario.bloqueadoHasta, ahora)
            )
        }

        val correcta = withContext(Dispatchers.Default) {
            Seguridad.verificar(clave, usuario.sal, usuario.claveHash, usuario.versionHash)
        }

        if (!correcta) {
            val estado = PoliticaBloqueo.tras(usuario.intentosFallidos, ahora)
            usuarioDao.actualizarIntentos(usuario.id, estado.intentosFallidos, estado.bloqueadoHasta)

            return if (PoliticaBloqueo.estaBloqueado(estado.bloqueadoHasta, ahora)) {
                ResultadoAuth.Bloqueado(
                    PoliticaBloqueo.segundosRestantes(estado.bloqueadoHasta, ahora)
                )
            } else {
                ResultadoAuth.Invalidas
            }
        }

        if (usuario.intentosFallidos != 0 || usuario.bloqueadoHasta != 0L) {
            usuarioDao.actualizarIntentos(usuario.id, 0, 0L)
        }

        if (Seguridad.necesitaActualizar(usuario.versionHash)) {
            val nuevaSal = Seguridad.generarSal()
            val nuevoHash = withContext(Dispatchers.Default) {
                Seguridad.hashear(clave, nuevaSal)
            }
            usuarioDao.actualizarHash(usuario.id, nuevoHash, nuevaSal, Seguridad.VERSION_ACTUAL)
        }

        return ResultadoAuth.Exito(usuario)
    }

    suspend fun obtenerPorId(id: Int): UsuarioEntity? {
        return usuarioDao.buscarPorId(id)
    }
}
