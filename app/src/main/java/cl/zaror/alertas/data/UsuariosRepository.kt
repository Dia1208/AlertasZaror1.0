package cl.zaror.alertas.data

import cl.zaror.alertas.domain.Seguridad

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

        val sal = Seguridad.generarSal()
        val usuario = UsuarioEntity(
            nombre = nombre.trim(),
            telefono = telefonoLimpio,
            direccion = direccion.trim(),
            claveHash = Seguridad.hashear(clave, sal),
            sal = sal
        )

        return usuarioDao.insertar(usuario).toInt()
    }

    /** Devuelve el usuario si el teléfono y la clave son correctos, o null si no. */
    suspend fun autenticar(telefono: String, clave: String): UsuarioEntity? {
        val usuario = usuarioDao.buscarPorTelefono(telefono.trim()) ?: return null

        return if (Seguridad.verificar(clave, usuario.sal, usuario.claveHash)) {
            usuario
        } else {
            null
        }
    }

    suspend fun obtenerPorId(id: Int): UsuarioEntity? {
        return usuarioDao.buscarPorId(id)
    }
}