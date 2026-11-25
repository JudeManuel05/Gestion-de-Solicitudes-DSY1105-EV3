package cl.duoc.gestionsolicitudes.repository

import cl.duoc.gestionsolicitudes.data.remote.RetrofitService
import cl.duoc.gestionsolicitudes.data.remote.api.IndicadoresApi
import cl.duoc.gestionsolicitudes.data.repository.IndicadoresRepository
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Prueba el consumo de la API externa mindicador.cl con un servidor falso. */
class IndicadoresRepositoryTest {

    private lateinit var servidor: MockWebServer
    private lateinit var repository: IndicadoresRepository

    @Before
    fun setUp() {
        servidor = MockWebServer()
        servidor.start()
        val api = RetrofitService.crearRetrofit(servidor.url("/").toString(), OkHttpClient())
            .create(IndicadoresApi::class.java)
        repository = IndicadoresRepository(api)
    }

    @After
    fun tearDown() {
        servidor.shutdown()
    }

    @Test
    fun transformaLaRespuestaEnUnaListaDeIndicadores() = runTest {
        servidor.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {"fecha":"2026-11-25T10:00:00.000Z",
                 "uf":{"codigo":"uf","nombre":"Unidad de fomento (UF)","unidad_medida":"Pesos","fecha":"2026-11-25","valor":41405.45},
                 "dolar":{"codigo":"dolar","nombre":"Dólar observado","unidad_medida":"Pesos","fecha":"2026-11-25","valor":1025.06}}
                """.trimIndent()
            )
        )

        val (fecha, indicadores) = repository.obtenerIndicadores().getOrThrow()

        assertEquals("2026-11-25T10:00:00.000Z", fecha)
        assertEquals(2, indicadores.size)            // euro y utm no venían: se omiten
        assertEquals(41405.45, indicadores[0].valor, 0.001)
        assertEquals("/api", servidor.takeRequest().path)
    }

    @Test
    fun servidorCaidoDevuelveFallo() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(500))

        assertTrue(repository.obtenerIndicadores().isFailure)
    }
}
