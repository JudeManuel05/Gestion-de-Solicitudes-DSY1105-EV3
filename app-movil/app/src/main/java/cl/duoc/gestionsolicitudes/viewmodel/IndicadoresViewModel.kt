package cl.duoc.gestionsolicitudes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionsolicitudes.data.repository.IndicadoresRepository
import cl.duoc.gestionsolicitudes.model.IndicadoresUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** ViewModel de la API externa: valores UF, dólar, euro y UTM para cotizar servicios. */
class IndicadoresViewModel(private val repository: IndicadoresRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(IndicadoresUiState())
    val uiState: StateFlow<IndicadoresUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            repository.obtenerIndicadores()
                .onSuccess { (fecha, lista) ->
                    _uiState.value = IndicadoresUiState(indicadores = lista, fecha = fecha.take(10))
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
        }
    }
}
