package cl.duoc.gestionsolicitudes.viewmodel

import cl.duoc.gestionsolicitudes.data.remote.ApiException
import cl.duoc.gestionsolicitudes.data.remote.dto.UsuarioDto
import cl.duoc.gestionsolicitudes.data.repository.UsuarioRepository
import cl.duoc.gestionsolicitudes.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas del LoginViewModel.
 * Se MOCKEA el UsuarioRepository con MockK: no se llama al microservicio real.
 */
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        repository = mockk()                 // repositorio falso
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun correoVacioMuestraErrorYNoLlamaAlServidor() {
        viewModel.onCorreoChange("")
        viewModel.onClaveChange("demo123")

        viewModel.iniciarSesion()

        assertEquals("El correo es obligatorio", viewModel.uiState.value.correoError)
        coVerify(exactly = 0) { repository.autenticar(any(), any()) }
    }

    @Test
    fun correoConFormatoInvalidoMuestraError() {
        viewModel.onCorreoChange("cliente.demo.cl")
        viewModel.onClaveChange("demo123")

        assertFalse(viewModel.validarFormulario())
        assertEquals("Formato de correo inválido", viewModel.uiState.value.correoError)
    }

    @Test
    fun claveCortaMuestraError() {
        viewModel.onCorreoChange("cliente@demo.cl")
        viewModel.onClaveChange("123")

        assertFalse(viewModel.validarFormulario())
        assertEquals("Debe tener al menos 6 caracteres", viewModel.uiState.value.claveError)
    }

    @Test
    fun loginCorrectoGuardaNombreYMarcaExito() = runTest {
        // Arrange: el mock responde como lo haría ms-usuarios
        coEvery { repository.autenticar("cliente@demo.cl", "demo123") } returns
                Result.success(UsuarioDto(1, "Cliente Demo", "cliente@demo.cl", "CLIENTE"))
        viewModel.onCorreoChange("cliente@demo.cl")
        viewModel.onClaveChange("demo123")

        // Act
        viewModel.iniciarSesion()

        // Assert
        val estado = viewModel.uiState.value
        assertTrue(estado.loginExitoso)
        assertEquals("Cliente Demo", estado.usuarioNombre)
        assertFalse(estado.cargando)
    }

    @Test
    fun loginRechazadoMuestraMensajeDelServidor() = runTest {
        coEvery { repository.autenticar(any(), any()) } returns
                Result.failure(ApiException(401, "Correo o contraseña incorrectos"))
        viewModel.onCorreoChange("cliente@demo.cl")
        viewModel.onClaveChange("otraclave")

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertFalse(estado.loginExitoso)
        assertEquals("Correo o contraseña incorrectos", estado.mensajeError)
    }

    @Test
    fun cerrarSesionLimpiaElEstado() {
        viewModel.onCorreoChange("cliente@demo.cl")
        viewModel.cerrarSesion()

        assertEquals("", viewModel.uiState.value.correo)
        assertNull(viewModel.uiState.value.mensajeError)
    }
}
