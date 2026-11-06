package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.zaror.alertas.data.ResultadoAuth
import cl.zaror.alertas.data.SesionManager
import cl.zaror.alertas.data.UsuariosRepository
import cl.zaror.alertas.domain.AdminsFijos
import cl.zaror.alertas.domain.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Datos del usuario logueado que pueden usar las pantallas (sin la contraseña). */
data class UsuarioSesion(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val rol: Rol
)

sealed interface EstadoSesion {
    /** Todavía se está leyendo la sesión guardada. */
    data object Cargando : EstadoSesion

    /** No hay sesión iniciada: se debe mostrar el login. */
    data object SinSesion : EstadoSesion

    /** Hay un usuario logueado. */
    data class Activa(val usuario: UsuarioSesion) : EstadoSesion
}

class AuthViewModel(
    private val usuarios: UsuariosRepository,
    private val sesion: SesionManager
) : ViewModel() {

    val estadoSesion: StateFlow<EstadoSesion> =
        sesion.usuarioId
            .map { id ->
                val usuario = id?.let { usuarios.obtenerPorId(it) }
                if (usuario == null) {
                    EstadoSesion.SinSesion
                } else {
                    EstadoSesion.Activa(
                        UsuarioSesion(
                            id = usuario.id,
                            nombre = usuario.nombre,
                            telefono = usuario.telefono,
                            direccion = usuario.direccion,
                            rol = AdminsFijos.rolPara(usuario.telefono)
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = EstadoSesion.Cargando
            )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    fun registrar(
        nombre: String,
        telefono: String,
        direccion: String,
        clave: String
    ) {
        viewModelScope.launch {
            _procesando.value = true
            _error.value = null

            val id = usuarios.registrar(nombre, telefono, direccion, clave)
            if (id == null) {
                _error.value = "Este teléfono ya está registrado"
            } else {
                sesion.iniciarSesion(id)
            }

            _procesando.value = false
        }
    }

    fun iniciarSesion(telefono: String, clave: String) {
        viewModelScope.launch {
            _procesando.value = true
            _error.value = null

            when (val resultado = usuarios.autenticar(telefono, clave)) {
                is ResultadoAuth.Exito -> sesion.iniciarSesion(resultado.usuario.id)

                ResultadoAuth.Invalidas ->
                    _error.value = "Teléfono o contraseña incorrectos"

                is ResultadoAuth.Bloqueado -> {
                    val minutos = (resultado.segundos + 59) / 60
                    _error.value =
                        "Demasiados intentos fallidos. Intenta de nuevo en $minutos min"
                }
            }

            _procesando.value = false
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sesion.cerrarSesion()
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}