package cl.duoc.gestionsolicitudes.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionsolicitudes.model.Indicador
import cl.duoc.gestionsolicitudes.utils.formatearPesos
import cl.duoc.gestionsolicitudes.viewmodel.IndicadoresViewModel

/**
 * Pantalla de la API EXTERNA (mindicador.cl).
 * Sirve como referencia para cotizar servicios en UF o dólares.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    viewModel: IndicadoresViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    // Se consulta la API al abrir la pantalla
    LaunchedEffect(Unit) { viewModel.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indicadores económicos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::cargar) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                estado.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                estado.error != null -> Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(estado.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::cargar) { Text("Reintentar") }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Valores de referencia para cotizar servicios. Fuente: mindicador.cl" +
                                    if (estado.fecha.isNotBlank()) " (${estado.fecha})" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(estado.indicadores, key = { it.codigo }) { indicador ->
                        AnimatedVisibility(visible = true, enter = fadeIn()) {
                            TarjetaIndicador(indicador)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaIndicador(indicador: Indicador) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(indicador.nombre, style = MaterialTheme.typography.titleMedium)
                Text(indicador.codigo.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                formatearPesos(indicador.valor),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
