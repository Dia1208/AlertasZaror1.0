package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.zaror.alertas.data.Alerta
import cl.zaror.alertas.data.AlertasRepository
import cl.zaror.alertas.domain.Permisos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertasViewModel(
    private val repositorio: AlertasRepository
) : ViewModel() {

    /**
     * Alertas que el usuario tiene permiso de ver:
     * el administrador ve todas; el vecino solo las suyas.
     */
    fun alertasVisibles(usuario: UsuarioSesion): Flow<List<Alerta>> {
        return if (Permisos.puedeVerTodas(usuario.rol)) {
            repositorio.observarTodas()
        } else {
            repositorio.observarDeUsuario(usuario.id)
        }
    }

    /** Crea una alerta identificando siempre al usuario de la sesión. */
    fun registrarAlerta(
        usuario: UsuarioSesion,
        tipo: String,
        descripcion: String,
        ubicacion: String,
        fotoUri: String? = null
    ) {
        viewModelScope.launch {
            repositorio.guardarAlerta(
                Alerta(
                    tipo = tipo,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    fotoUri = fotoUri,
                    reportadaPor = usuario.nombre,
                    usuarioId = usuario.id
                )
            )
        }
    }

    fun marcarComoAtendida(usuario: UsuarioSesion, id: Int) {
        if (!Permisos.puedeMarcarAtendida(usuario.rol)) return
        viewModelScope.launch {
            repositorio.marcarAtendida(id)
        }
    }

    fun cancelarAlerta(usuario: UsuarioSesion, id: Int) {
        viewModelScope.launch {
            repositorio.cancelarPropia(id, usuario.id)
        }
    }

    fun eliminarAlerta(usuario: UsuarioSesion, id: Int) {
        if (!Permisos.puedeEliminar(usuario.rol)) return
        viewModelScope.launch {
            repositorio.eliminarAlerta(id)
        }
    }
}
