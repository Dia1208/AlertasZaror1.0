package cl.zaror.alertas.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactosRepository(
    private val contactoDao: ContactoDao
) {

    fun observar(usuarioId: Int): Flow<List<Contacto>> {
        return contactoDao.observarDeUsuario(usuarioId).map { lista ->
            lista.map { Contacto(it.id, it.usuarioId, it.nombre, it.telefono) }
        }
    }

    /** Devuelve true si se agregó, o false si ya estaba en la lista. */
    suspend fun agregar(usuarioId: Int, nombre: String, telefono: String): Boolean {
        val id = contactoDao.insertar(
            ContactoEntity(
                usuarioId = usuarioId,
                nombre = nombre.trim(),
                telefono = telefono.trim()
            )
        )
        return id != -1L
    }

    suspend fun eliminar(usuarioId: Int, id: Int) {
        contactoDao.eliminar(id, usuarioId)
    }
}
