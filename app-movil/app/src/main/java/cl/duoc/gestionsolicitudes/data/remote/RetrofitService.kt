package cl.duoc.gestionsolicitudes.data.remote

import cl.duoc.gestionsolicitudes.BuildConfig
import cl.duoc.gestionsolicitudes.data.remote.api.IndicadoresApi
import cl.duoc.gestionsolicitudes.data.remote.api.SolicitudApi
import cl.duoc.gestionsolicitudes.data.remote.api.UsuarioApi
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitService: crea UNA instancia de Retrofit por cada servidor.
 *  - ms-usuarios    (microservicio propio, puerto 8081)
 *  - ms-solicitudes (microservicio propio, puerto 8082)
 *  - mindicador.cl  (API externa pública)
 * "by lazy" hace que cada cliente se cree solo la primera vez que se usa.
 */
object RetrofitService {

    // OkHttp: cliente HTTP compartido. El interceptor imprime cada petición en el Logcat.
    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS) // mindicador.cl a veces responde lento
            .build()
    }

    /** Construye Retrofit con la URL base y el convertidor JSON <-> objetos Kotlin (Gson). */
    fun crearRetrofit(baseUrl: String, client: OkHttpClient = cliente): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val usuarioApi: UsuarioApi by lazy {
        crearRetrofit(BuildConfig.URL_MS_USUARIOS).create(UsuarioApi::class.java)
    }

    val solicitudApi: SolicitudApi by lazy {
        crearRetrofit(BuildConfig.URL_MS_SOLICITUDES).create(SolicitudApi::class.java)
    }

    val indicadoresApi: IndicadoresApi by lazy {
        crearRetrofit(BuildConfig.URL_API_EXTERNA).create(IndicadoresApi::class.java)
    }
}
