package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.zaror.alertas.data.SesionManager
import cl.zaror.alertas.data.TokenManager
import cl.zaror.alertas.data.UsuarioDao
import cl.zaror.alertas.data.UsuarioEntity
import cl.zaror.alertas.data.UsuariosRepository
import cl.zaror.alertas.data.remote.AuthApi
import cl.zaror.alertas.data.remote.LoginRequest
import cl.zaror.alertas.data.remote.RegistroRequest
import cl.zaror.alertas.domain.Rol
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

data class UsuarioSesion(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val rol: Rol
)

sealed interface EstadoSesion {
    data object Cargando : EstadoSesion
    data object SinSesion : EstadoSesion
    data class Activa(val usuario: UsuarioSesion) : EstadoSesion
}

class AuthViewModel(
    private val usuarios: UsuariosRepository,
    private val sesion: SesionManager,
    private val usuarioDao: UsuarioDao,
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    val estadoSesion: StateFlow<EstadoSesion> =
        combine(sesion.usuarioId, tokenManager.rol) { id, rol ->
            id to rol
        }.map { (id, rolServidor) ->

            val usuario = id?.let { usuarios.obtenerPorId(it) }

            if (usuario == null) {
                EstadoSesion.SinSesion
            } else {
                val rol = if (rolServidor.equals("ADMIN", true) ||
                    rolServidor.equals("ADMINISTRADOR", true)
                ) {
                    Rol.ADMINISTRADOR
                } else {
                    Rol.VECINO
                }

                EstadoSesion.Activa(
                    UsuarioSesion(
                        id = usuario.id,
                        nombre = usuario.nombre,
                        telefono = usuario.telefono,
                        direccion = usuario.direccion,
                        rol = rol
                    )
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            EstadoSesion.Cargando
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

            try {
                val respuesta = api.registrar(
                    RegistroRequest(
                        nombre = nombre,
                        telefono = telefono,
                        direccion = direccion,
                        clave = clave
                    )
                )

                _error.value =
                    if (respuesta.estado == "PENDIENTE") {
                        "Registro enviado. Tu cuenta está pendiente de aprobación por un administrador."
                    } else {
                        "Registro recibido correctamente."
                    }

            } catch (e: Exception) {
                _error.value = mensajeError(e)
            } finally {
                _procesando.value = false
            }
        }
    }

    fun iniciarSesion(telefono: String, clave: String) {
        viewModelScope.launch {
            _procesando.value = true
            _error.value = null

            try {
                val respuestaLogin = api.login(
                    LoginRequest(telefono, clave)
                )

                val perfil = api.obtenerPerfil(
                    "${respuestaLogin.tipo} ${respuestaLogin.token}"
                )

                val existente = usuarioDao.buscarPorTelefono(perfil.telefono)

                val idLocal = if (existente != null) {
                    usuarioDao.actualizarPerfilLocal(
                        existente.id,
                        perfil.nombre,
                        existente.direccion
                    )
                    existente.id
                } else {
                    usuarioDao.insertar(
                        UsuarioEntity(
                            nombre = perfil.nombre,
                            telefono = perfil.telefono,
                            direccion = "",
                            claveHash = UUID.randomUUID().toString(),
                            sal = UUID.randomUUID().toString()
                        )
                    ).toInt()
                }

                tokenManager.guardar(
                    respuestaLogin.token,
                    perfil.rol
                )

                sesion.iniciarSesion(idLocal)

            } catch (e: Exception) {
                _error.value = mensajeError(e)
            } finally {
                _procesando.value = false
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            tokenManager.limpiar()
            sesion.cerrarSesion()
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    private fun mensajeError(e: Exception): String {
        if (e is HttpException) {
            val cuerpo = e.response()?.errorBody()?.string()

            if (!cuerpo.isNullOrBlank()) {
                try {
                    val json = JsonParser.parseString(cuerpo).asJsonObject

                    if (json.has("error")) {
                        return json.get("error").asString
                    }
                } catch (_: Exception) {
                    // Se utiliza el mensaje HTTP genérico.
                }
            }

            return when (e.code()) {
                401 -> "Teléfono o contraseña incorrectos"
                403 -> "Tu cuenta aún no está aprobada"
                409 -> "Este teléfono ya está registrado"
                429 -> "Demasiados intentos. Intenta más tarde."
                else -> "Error del servidor: ${e.code()}"
            }
        }

        return when (e) {
            is java.net.UnknownHostException,
            is java.net.ConnectException -> "No se pudo conectar con el servidor."
            else -> e.message ?: "Ocurrió un error al iniciar sesión."
        }
    }
}