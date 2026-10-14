package cl.zaror.alertas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.zaror.alertas.data.Alerta
import cl.zaror.alertas.data.AlertasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertasViewModel(
    private val repositorio: AlertasRepository
) : ViewModel() {

    val alertas: StateFlow<List<Alerta>> =
        repositorio.observarAlertas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun guardarAlerta(alerta: Alerta) {
        viewModelScope.launch {
            repositorio.guardarAlerta(alerta)
        }
    }

    fun marcarComoAtendida(id: Int) {
        viewModelScope.launch {
            repositorio.actualizarEstado(
                id = id,
                nuevoEstado = "Atendida"
            )
        }
    }

    fun eliminarAlerta(id: Int) {
        viewModelScope.launch {
            repositorio.eliminarAlerta(id)
        }
    }
}
