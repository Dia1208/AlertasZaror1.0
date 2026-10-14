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
    private val repository: AlertasRepository
) : ViewModel() {

    val alertas: StateFlow<List<Alerta>> =
        repository.alertas.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun guardarAlerta(alerta: Alerta) {
        viewModelScope.launch {
            repository.insertar(alerta)
        }
    }

    fun resolverAlerta(id: Int) {
        viewModelScope.launch {
            repository.marcarComoResuelta(id)
        }
    }
}
