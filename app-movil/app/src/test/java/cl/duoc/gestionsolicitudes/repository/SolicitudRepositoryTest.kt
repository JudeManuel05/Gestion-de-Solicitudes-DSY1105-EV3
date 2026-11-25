package cl.duoc.gestionsolicitudes.repository

import cl.duoc.gestionsolicitudes.data.remote.ApiException
import cl.duoc.gestionsolicitudes.data.remote.RetrofitService
import cl.duoc.gestionsolicitudes.data.remote.api.SolicitudApi
import cl.duoc.gestionsolicitudes.data.remote.dto.SolicitudRequest
import cl.duoc.gestionsolicitudes.data.repository.SolicitudRepository
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Prueba la integración Retrofit <-> microservicio SIN levantar Spring Boot:
 * MockWebServer es un servidor HTTP falso que devuelve el JSON que le indicamos.
 */
class SolicitudRepositoryTest {

    private lateinit var servidor: MockWebServer
    private lateinit var repository: SolicitudRepository

    private val jsonSolicitud = """
        {"id":1,"codigo":"SOL-1A2B3C4D","titulo":"Sitio web caído",
         "descripcion":"El sitio no carga desde ayer","categoria":"Incidente técnico",
         "servicio":"www.cliente.cl","evidenciaUri":null,"estado":"Recibida",
         "correoCliente":"cliente@demo.cl","fechaCreacion":1700000000000,"fechaActualizacion":1700000000000}
    """.trimIndent()

    @Before
    fun setUp() {
        servidor = MockWebServer()
        servidor.start()
        val api = RetrofitService.crearRetrofit(servidor.url("/").toString(), OkHttpClient())
            .create(SolicitudApi::class.java)
        repository = SolicitudRepository(api)
    }

    @After
    fun tearDown() {
        servidor.shutdown()
    }

    @Test
    fun listarConvierteElJsonEnObjetos() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody("[$jsonSolicitud]"))

        val resultado = repository.listar()

        val lista = resultado.getOrThrow()
        assertEquals(1, lista.size)
        assertEquals("SOL-1A2B3C4D", lista[0].codigo)
        assertEquals("GET", servidor.takeRequest().method)
    }

    @Test
    fun crearEnviaPostAlEndpointCorrecto() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(201).setBody(jsonSolicitud))
        val request = SolicitudRequest("Sitio web caído", "El sitio no carga desde ayer",
            "Incidente técnico", "www.cliente.cl", null, "cliente@demo.cl")

        val resultado = repository.crear(request)

        assertTrue(resultado.isSuccess)
        val peticion = servidor.takeRequest()
        assertEquals("POST", peticion.method)
        assertEquals("/api/solicitudes", peticion.path)
        assertTrue(peticion.body.readUtf8().contains("\"titulo\":\"Sitio web caído\""))
    }

    @Test
    fun errorDelMicroservicioSeConvierteEnMensaje() = runTest {
        servidor.enqueue(
            MockResponse().setResponseCode(400)
                .setBody("{\"mensaje\":\"Indica el servicio o sitio relacionado\"}")
        )

        val resultado = repository.crear(
            SolicitudRequest("Sitio web caído", "El sitio no carga desde ayer", "Hosting", "", null, null)
        )

        val error = resultado.exceptionOrNull() as ApiException
        assertEquals(400, error.codigo)
        assertEquals("Indica el servicio o sitio relacionado", error.message)
    }

    @Test
    fun eliminarUsaDeleteYAceptaRespuestaSinCuerpo() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(204))

        val resultado = repository.eliminar(7)

        assertTrue(resultado.isSuccess)
        val peticion = servidor.takeRequest()
        assertEquals("DELETE", peticion.method)
        assertEquals("/api/solicitudes/7", peticion.path)
    }
}
