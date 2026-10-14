package cl.zaror.alertas.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertasRepository(
    private val alertaDao: AlertaDao
) {

    val alertas: Flow<List<Alerta>> =
        alertaDao.observarTodas().map { lista ->
            lista.map { entidad ->
                Alerta(
                    id = entidad.id,
                    tipo = entidad.tipo,
                    descripcion = entidad.descripcion,
                    ubicacion = entidad.ubicacion,
                    fotoUri = entidad.fotoUri,
                    estado = entidad.estado,
                    fechaHora = entidad.fechaHora,
                    reportadaPor = entidad.reportadaPor
                )
            }
        }

    suspend fun insertar(alerta: Alerta) {
        alertaDao.insertar(
            AlertaEntity(
                id = alerta.id,
                tipo = alerta.tipo,
                descripcion = alerta.descripcion,
                ubicacion = alerta.ubicacion,
                fotoUri = alerta.fotoUri,
                estado = alerta.estado,
                fechaHora = alerta.fechaHora,
                reportadaPor = alerta.reportadaPor
            )
        )
    }

    suspend fun marcarComoResuelta(id: Int) {
        alertaDao.actualizarEstado(id, "Resuelta")
    }
}
