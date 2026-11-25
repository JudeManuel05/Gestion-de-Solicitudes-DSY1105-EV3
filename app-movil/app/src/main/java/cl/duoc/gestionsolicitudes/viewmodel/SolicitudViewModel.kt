package cl.duoc.gestionsolicitudes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionsolicitudes.data.remote.dto.SolicitudRequest
import cl.duoc.gestionsolicitudes.data.repository.SolicitudRepository
import cl.duoc.gestionsolicitudes.model.ListaUiState
import cl.duoc.gestionsolicitudes.model.Solicitud
import cl.duoc.gestionsolicitudes.model.SolicitudUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del módulo de solicitudes (lista + formulario).
 * View -> ViewModel -> Repository -> Retrofit -> ms-solicitudes -> Base de datos
 *
 * @param notificar función que muestra una notificación local. Se recibe por
 * constructor para no depender de Android y poder probar el ViewModel con JUnit.
 */
class SolicitudViewModel(
    private val repository: SolicitudRepository,
    private val notificar: (titulo: String, mensaje: String) -> Unit = { _, _ -> }
) : ViewModel() {

    // Correo del usuario que inició sesión (se guarda en cada solicitud nueva)
    private var correoUsuario: String? = null

    private val _lista = MutableStateFlow(ListaUiState())
    val lista: StateFlow<ListaUiState> = _lista.asStateFlow()

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun configurarUsuario(correo: String) {
        correoUsuario = correo.ifBlank { null }
    }

    // ---------- LISTA (GET /api/solicitudes) ----------
    fun cargarSolicitudes() {
        viewModelScope.launch {
            _lista.update { it.copy(cargando = true, error = null) }
            repository.listar()
                .onSuccess { datos -> _lista.update { ListaUiState(solicitudes = datos) } }
                .onFailure { e -> _lista.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    // ---------- FORMULARIO ----------
    /** id = 0 prepara un formulario vacío; otro id trae la solicitud desde el microservicio. */
    fun cargarSolicitud(id: Long) {
        if (id == 0L) {
            _uiState.value = SolicitudUiState()
            return
        }
        viewModelScope.launch {
            _uiState.value = SolicitudUiState(id = id, cargando = true)
            repository.obtenerPorId(id)
                .onSuccess { s ->
                    _uiState.value = SolicitudUiState(
                        id = s.id,
                        codigo = s.codigo,
                        titulo = s.titulo,
                        descripcion = s.descripcion,
                        categoria = s.categoria,
                        servicio = s.servicio.orEmpty(),
                        evidenciaUri = s.evidenciaUri,
                        estado = s.estado,
                        estadoOriginal = s.estado,
                        fechaCreacion = s.fechaCreacion
                    )
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, mensajeError = e.message) } }
        }
    }

    // Eventos de la View: actualizan el valor y limpian el error de ese campo
    fun onTituloChange(valor: String) {
        _uiState.update { it.copy(titulo = valor, tituloError = null) }
    }

    fun onDescripcionChange(valor: String) {
        _uiState.update { it.copy(descripcion = valor, descripcionError = null) }
    }

    fun onCategoriaChange(valor: String) {
        _uiState.update { it.copy(categoria = valor, categoriaError = null, servicioError = null) }
    }

    fun onServicioChange(valor: String) {
        _uiState.update { it.copy(servicio = valor, servicioError = null) }
    }

    fun onEstadoChange(valor: String) {
        _uiState.update { it.copy(estado = valor) }
    }

    fun onEvidenciaCapturada(uri: String) {
        _uiState.update { it.copy(evidenciaUri = uri) }
    }

    fun quitarEvidencia() {
        _uiState.update { it.copy(evidenciaUri = null) }
    }

    fun limpiarError() {
        _uiState.update { it.copy(mensajeError = null) }
    }

    // REGLAS DEL NEGOCIO: validación centralizada, la pantalla solo muestra los errores
    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        val tituloError = when {
            estado.titulo.isBlank() -> "El título es obligatorio"
            estado.titulo.trim().length < 5 -> "El título debe tener al menos 5 caracteres"
            else -> null
        }
        val descripcionError = when {
            estado.descripcion.isBlank() -> "La descripción es obligatoria"
            estado.descripcion.trim().length < 10 -> "Describe la necesidad (mínimo 10 caracteres)"
            else -> null
        }
        val categoriaError = if (estado.categoria.isBlank()) "Selecciona una categoría" else null
        val servicioError = if (estado.servicioObligatorio && estado.servicio.isBlank()) {
            "Indica el servicio o sitio relacionado"
        } else null

        _uiState.update {
            it.copy(
                tituloError = tituloError,
                descripcionError = descripcionError,
                categoriaError = categoriaError,
                servicioError = servicioError
            )
        }
        return listOf(tituloError, descripcionError, categoriaError, servicioError).all { it == null }
    }

    /** INSERTAR (POST) o ACTUALIZAR (PUT + PATCH de estado) en el microservicio. */
    fun guardar() {
        if (!validarFormulario()) return
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, mensajeError = null) }
            val estado = _uiState.value
            val request = SolicitudRequest(
                titulo = estado.titulo.trim(),
                descripcion = estado.descripcion.trim(),
                categoria = estado.categoria,
                servicio = estado.servicio.trim(),
                evidenciaUri = estado.evidenciaUri,
                correoCliente = correoUsuario
            )

            val resultado = if (!estado.esEdicion) {
                repository.crear(request)
            } else {
                repository.actualizar(estado.id, request).mapCatching { actualizada ->
                    // Si el usuario cambió el estado, se usa el endpoint PATCH /estado
                    if (estado.estado != estado.estadoOriginal) {
                        repository.cambiarEstado(estado.id, estado.estado).getOrThrow()
                    } else actualizada
                }
            }

            resultado
                .onSuccess { guardada ->
                    val accion = if (estado.esEdicion) "actualizada" else "recibida"
                    notificar("Solicitud ${guardada.codigo} $accion", guardada.titulo)
                    _uiState.update {
                        it.copy(codigo = guardada.codigo, guardando = false, guardadoExitosamente = true)
                    }
                    cargarSolicitudes()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(guardando = false, mensajeError = e.message) }
                }
        }
    }

    /** ELIMINAR (DELETE /api/solicitudes/{id}). */
    fun eliminar() {
        val estado = _uiState.value
        if (!estado.esEdicion) return
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, mensajeError = null) }
            repository.eliminar(estado.id)
                .onSuccess {
                    notificar("Solicitud ${estado.codigo} eliminada", estado.titulo)
                    _uiState.update { it.copy(guardando = false, eliminadoExitosamente = true) }
                    cargarSolicitudes()
                }
                .onFailure { e -> _uiState.update { it.copy(guardando = false, mensajeError = e.message) } }
        }
    }

    /** La pantalla avisa que ya reaccionó al guardado/eliminado (navegó atrás). */
    fun onGuardadoConsumido() {
        _uiState.update { it.copy(guardadoExitosamente = false, eliminadoExitosamente = false) }
    }

    /** Usado por el lector QR: GET /api/solicitudes/codigo/{codigo}. null si no existe. */
    suspend fun buscarPorCodigo(codigo: String): Solicitud? =
        repository.obtenerPorCodigo(codigo).getOrNull()
}
