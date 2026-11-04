package cl.zaror.alertas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sesionDataStore by preferencesDataStore(name = "sesion")

/** Guarda en el teléfono qué usuario tiene la sesión iniciada. */
class SesionManager(context: Context) {

    private val almacen = context.applicationContext.sesionDataStore

    private val claveUsuarioId = intPreferencesKey("usuario_id")

    /** Id del usuario logueado, o null si no hay sesión iniciada. */
    val usuarioId: Flow<Int?> = almacen.data.map { preferencias ->
        preferencias[claveUsuarioId]
    }

    suspend fun iniciarSesion(id: Int) {
        almacen.edit { preferencias ->
            preferencias[claveUsuarioId] = id
        }
    }

    suspend fun cerrarSesion() {
        almacen.edit { preferencias ->
            preferencias.remove(claveUsuarioId)
        }
    }
}