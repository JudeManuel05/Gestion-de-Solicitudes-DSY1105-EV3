package cl.duoc.gestionsolicitudes.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionsolicitudes.data.remote.RetrofitService
import cl.duoc.gestionsolicitudes.data.repository.IndicadoresRepository
import cl.duoc.gestionsolicitudes.data.repository.SolicitudRepository
import cl.duoc.gestionsolicitudes.data.repository.UsuarioRepository
import cl.duoc.gestionsolicitudes.utils.NotificationHelper

/**
 * Fábrica de ViewModels: aquí se "arman" las dependencias reales
 * (Retrofit -> Repository -> ViewModel). En las pruebas se usan mocks en su lugar.
 */
fun appViewModelFactory(app: Application): ViewModelProvider.Factory = viewModelFactory {
    initializer { LoginViewModel(UsuarioRepository(RetrofitService.usuarioApi)) }
    initializer {
        SolicitudViewModel(SolicitudRepository(RetrofitService.solicitudApi)) { titulo, mensaje ->
            NotificationHelper.mostrar(app, titulo, mensaje)
        }
    }
    initializer { IndicadoresViewModel(IndicadoresRepository(RetrofitService.indicadoresApi)) }
    initializer { QrViewModel() }
}
