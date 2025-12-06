package cl.inacap.cloudled.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.inacap.cloudled.datos.FirebaseRepositorio
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class EstadoUi(
    val led: String = "APAGADO"   // "encendido" | "apagado"
)

class MonitorViewModel(
    private val repositorio: FirebaseRepositorio = FirebaseRepositorio()
) : ViewModel() {

    val estadoUi: StateFlow<EstadoUi> =
        repositorio.observarEstado()
            .map { EstadoUi(led = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = EstadoUi()
            )

    fun alternarLed() = viewModelScope.launch {
        repositorio.alternarEstado()
    }
}

