package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.zaror.alertas.data.Contacto
import cl.zaror.alertas.data.ContactosRepository
import cl.zaror.alertas.domain.ValidacionesContacto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactosViewModel(
    private val repositorio: ContactosRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Contactos de confianza del usuario de la sesión (nunca los de otros). */
    fun contactos(usuario: UsuarioSesion): Flow<List<Contacto>> {
        return repositorio.observar(usuario.id)
    }

    /** Valida de nuevo en la lógica (no solo en la vista) y agrega al contacto. */
    fun agregar(
        usuario: UsuarioSesion,
        nombre: String,
        telefono: String,
        alExito: () -> Unit
    ) {
        val errorValidacion = ValidacionesContacto.validarNombre(nombre)
            ?: ValidacionesContacto.validarTelefono(telefono)
            ?: ValidacionesContacto.validarNoEsPropio(telefono, usuario.telefono)

        if (errorValidacion != null) {
            _error.value = errorValidacion
            return
        }

        viewModelScope.launch {
            val agregado = repositorio.agregar(usuario.id, nombre, telefono)
            if (agregado) {
                _error.value = null
                alExito()
            } else {
                _error.value = "Ese contacto ya está en tu lista"
            }
        }
    }

    fun eliminar(usuario: UsuarioSesion, id: Int) {
        viewModelScope.launch {
            repositorio.eliminar(usuario.id, id)
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
