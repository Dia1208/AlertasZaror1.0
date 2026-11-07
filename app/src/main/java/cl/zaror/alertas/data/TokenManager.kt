package cl.zaror.alertas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore(
    name = "sesion_remota"
)

class TokenManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("jwt_token")
    private val rolKey = stringPreferencesKey("rol_servidor")

    val rol: Flow<String?> = context.tokenDataStore.data.map {
        it[rolKey]
    }

    suspend fun guardar(token: String, rol: String) {
        context.tokenDataStore.edit {
            it[tokenKey] = token
            it[rolKey] = rol
        }
    }

    suspend fun limpiar() {
        context.tokenDataStore.edit {
            it.remove(tokenKey)
            it.remove(rolKey)
        }
    }

    val token: Flow<String?> = context.tokenDataStore.data.map {
        it[tokenKey]
    }
}