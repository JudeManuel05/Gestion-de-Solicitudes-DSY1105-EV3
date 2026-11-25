package cl.duoc.gestionsolicitudes.viewmodel

import cl.duoc.gestionsolicitudes.data.remote.ApiException
import cl.duoc.gestionsolicitudes.data.remote.dto.SolicitudRequest
import cl.duoc.gestionsolicitudes.data.repository.SolicitudRepository
import cl.duoc.gestionsolicitudes.model.Solicitud
import cl.duoc.gestionsolicitudes.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas del SolicitudViewModel: validaciones y persistencia remota.
 * Se mockea SolicitudRepository (MockK) y la función de notificación.
 */
class SolicitudViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: SolicitudRepository
    private lateinit var viewModel: SolicitudViewModel
    private val notificaciones = mutableListOf<String>()

    private fun solicitud(id: Long = 1, estado: String = "Recibida") = Solicitud(
        id = id, codigo = "SOL-1A2B3C4D", titulo = "Sitio web caído",
        descripcion = "El sitio no carga desde ayer", categoria = "Incidente técnico",
        servicio = "www.cliente.cl", evidenciaUri = null, estado = estado,
        correoCliente = "cliente@demo.cl", fechaCreacion = 0, fechaActualizacion = 0
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        coEvery { repository.listar() } returns Result.success(emptyList())
        viewModel = SolicitudViewModel(repository) { titulo, _ -> notificaciones.add(titulo) }
        viewModel.configurarUsuario("cliente@demo.cl")
    }

    private fun llenarFormularioValido() {
        viewModel.onTituloChange("Sitio web caído")
        viewModel.onDescripcionChange("El sitio no carga desde ayer")
        viewModel.onCategoriaChange("Incidente técnico")
        viewModel.onServicioChange("www.cliente.cl")
    }

    @Test
    fun formularioVacioMarcaTodosLosErrores() {
        assertFalse(viewModel.validarFormulario())

        val estado = viewModel.uiState.value
        assertEquals("El título es obligatorio", estado.tituloError)
        assertEquals("La descripción es obligatoria", estado.descripcionError)
        assertEquals("Selecciona una categoría", estado.categoriaError)
    }

    @Test
    fun servicioNoEsObligatorioEnCategoriaConsulta() {
        viewModel.onTituloChange("Consulta de precios")
        viewModel.onDescripcionChange("Quiero saber cuánto cuesta un hosting")
        viewModel.onCategoriaChange("Consulta")

        assertTrue(viewModel.validarFormulario())
        assertNull(viewModel.uiState.value.servicioError)
    }

    @Test
    fun guardarNuevaEnviaPostConElCorreoDelUsuario() = runTest {
        val enviado = slot<SolicitudRequest>()   // captura lo que se envía al servidor
        coEvery { repository.crear(capture(enviado)) } returns Result.success(solicitud())
        llenarFormularioValido()

        viewModel.guardar()

        assertEquals("Sitio web caído", enviado.captured.titulo)
        assertEquals("cliente@demo.cl", enviado.captured.correoCliente)
        assertTrue(viewModel.uiState.value.guardadoExitosamente)
        assertEquals("Solicitud SOL-1A2B3C4D recibida", notificaciones.first())
        coVerify { repository.listar() }          // la lista se refresca después de guardar
    }

    @Test
    fun formularioInvalidoNoLlamaAlMicroservicio() = runTest {
        viewModel.onTituloChange("Hola")          // muy corto

        viewModel.guardar()

        coVerify(exactly = 0) { repository.crear(any()) }
    }

    @Test
    fun errorDelServidorSeMuestraEnPantalla() = runTest {
        coEvery { repository.crear(any()) } returns
                Result.failure(ApiException(0, "No se pudo conectar con el servidor."))
        llenarFormularioValido()

        viewModel.guardar()

        assertEquals("No se pudo conectar con el servidor.", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.guardadoExitosamente)
    }

    @Test
    fun editarConCambioDeEstadoUsaPutYPatch() = runTest {
        coEvery { repository.obtenerPorId(1) } returns Result.success(solicitud())
        coEvery { repository.actualizar(1, any()) } returns Result.success(solicitud())
        coEvery { repository.cambiarEstado(1, "En proceso") } returns
                Result.success(solicitud(estado = "En proceso"))

        viewModel.cargarSolicitud(1)
        viewModel.onEstadoChange("En proceso")
        viewModel.guardar()

        coVerify { repository.actualizar(1, any()) }
        coVerify { repository.cambiarEstado(1, "En proceso") }
        assertTrue(viewModel.uiState.value.guardadoExitosamente)
    }

    @Test
    fun eliminarLlamaAlDeleteDelMicroservicio() = runTest {
        coEvery { repository.obtenerPorId(1) } returns Result.success(solicitud())
        coEvery { repository.eliminar(1) } returns Result.success(Unit)

        viewModel.cargarSolicitud(1)
        viewModel.eliminar()

        coVerify { repository.eliminar(1) }
        assertTrue(viewModel.uiState.value.eliminadoExitosamente)
    }

    @Test
    fun cargarSolicitudesLlenaLaLista() = runTest {
        coEvery { repository.listar() } returns Result.success(listOf(solicitud(1), solicitud(2)))

        viewModel.cargarSolicitudes()

        assertEquals(2, viewModel.lista.value.solicitudes.size)
        assertFalse(viewModel.lista.value.cargando)
    }
}
