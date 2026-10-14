package cl.zaror.alertas.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertasRepository(
    private val alertaDao: AlertaDao
) {

    fun observarAlertas(): Flow<List<Alerta>> {
        return alertaDao.observarTodas().map { lista ->
            lista.map { entidad ->
                entidad.toModelo()
            }
        }
    }

    suspend fun guardarAlerta(alerta: Alerta) {
        alertaDao.insertar(alerta.toEntidad())
    }

    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    ) {
        alertaDao.actualizarEstado(id, nuevoEstado)
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
            reportadaPor = reportadaPor
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
            reportadaPor = reportadaPor
        )
    }
}
