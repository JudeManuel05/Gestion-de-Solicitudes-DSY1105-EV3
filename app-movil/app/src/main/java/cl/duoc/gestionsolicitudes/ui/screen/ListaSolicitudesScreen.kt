package cl.duoc.gestionsolicitudes.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.gestionsolicitudes.model.Solicitud
import cl.duoc.gestionsolicitudes.utils.formatearFecha
import cl.duoc.gestionsolicitudes.viewmodel.SolicitudViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSolicitudesScreen(
    viewModel: SolicitudViewModel,
    onNuevaSolicitud: () -> Unit,
    onEditarSolicitud: (Long) -> Unit,
    onEscanearQr: () -> Unit,
    onVolver: () -> Unit
) {
    // Estado de la lista que entrega el ViewModel (datos del microservicio ms-solicitudes)
    val lista by viewModel.lista.collectAsState()
    val solicitudes = lista.solicitudes

    // Al entrar a la pantalla se piden las solicitudes: GET /api/solicitudes
    LaunchedEffect(Unit) { viewModel.cargarSolicitudes() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::cargarSolicitudes) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onEscanearQr) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear QR")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaSolicitud,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva solicitud") }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (lista.cargando) {
                LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }

            // Error de conexión con el microservicio
            AnimatedVisibility(
                visible = lista.error != null && solicitudes.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(lista.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::cargarSolicitudes) { Text("Reintentar") }
                }
            }

            // Estado vacío
            AnimatedVisibility(
                visible = solicitudes.isEmpty() && !lista.cargando && lista.error == null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Aún no hay solicitudes registradas")
                    Text(
                        "Presiona \"Nueva solicitud\" para crear la primera",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudes, key = { it.id }) { solicitud ->
                    SolicitudCard(
                        solicitud = solicitud,
                        onClick = { onEditarSolicitud(solicitud.id) },
                        // Animación: los ítems se desplazan suavemente al agregarse o reordenarse
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun SolicitudCard(
    solicitud: Solicitud,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    solicitud.codigo,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(solicitud.estado)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                solicitud.titulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Category,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    solicitud.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (solicitud.evidenciaUri != null) {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = "Tiene evidencia",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    formatearFecha(solicitud.fechaCreacion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}