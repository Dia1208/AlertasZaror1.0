package cl.zaror.alertas.data

import cl.zaror.alertas.domain.Permisos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertasRepository(
    private val alertaDao: AlertaDao
) {

    /** Todas las alertas. Solo debe usarse para administradores. */
    fun observarTodas(): Flow<List<Alerta>> {
        return alertaDao.observarTodas().map { lista ->
            lista.map { entidad ->
                entidad.toModelo()
            }
        }
    }

    /** Solo las alertas del usuario indicado. */
    fun observarDeUsuario(usuarioId: Int): Flow<List<Alerta>> {
        return alertaDao.observarDeUsuario(usuarioId).map { lista ->
            lista.map { entidad ->
                entidad.toModelo()
            }
        }
    }

    suspend fun guardarAlerta(alerta: Alerta) {
        alertaDao.insertar(alerta.toEntidad())
    }

    suspend fun marcarAtendida(id: Int) {
        alertaDao.actualizarEstado(id, Permisos.ESTADO_ATENDIDA)
    }

    /** Devuelve true si se canceló (era del usuario y estaba activa). */
    suspend fun cancelarPropia(id: Int, usuarioId: Int): Boolean {
        return alertaDao.cancelarPropia(id, usuarioId) > 0
    }

    suspend fun eliminarAlerta(id: Int) {
        alertaDao.eliminar(id)
    }

    private fun AlertaEntity.toModelo(): Alerta {
        return Alerta(
            id = id,
            tipo = tipo,
            descripcion = descripcion,
            ubicacion = ubicacion,
            fotoUri = fotoUri,
            estado = estado,
            fechaHora = fechaHora,
            reportadaPor = reportadaPor,
            usuarioId = usuarioId
        )
    }

    private fun Alerta.toEntidad(): AlertaEntity {
        return AlertaEntity(
            id = id,
            tipo = tipo,
            descripcion = descripcion,
            ubicacion = ubicacion,
            fotoUri = fotoUri,
            estado = estado,
            fechaHora = fechaHora,
            reportadaPor = reportadaPor,
            usuarioId = usuarioId
        )
    }
}
