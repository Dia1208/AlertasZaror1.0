package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.zaror.alertas.data.SesionManager
import cl.zaror.alertas.data.TokenManager
import cl.zaror.alertas.data.UsuarioDao
import cl.zaror.alertas.data.UsuariosRepository
import cl.zaror.alertas.data.remote.AuthApi

class AuthViewModelFactory(
    private val usuarios: UsuariosRepository,
    private val sesion: SesionManager,
    private val usuarioDao: UsuarioDao,
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                usuarios,
                sesion,
                usuarioDao,
                api,
                tokenManager
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}