package cl.zaror.alertas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.data.Alerta
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun iconoDeTipo(tipo: String): ImageVector = when (tipo) {
    "Emergencia" -> Icons.Default.NotificationsActive
    "Falla de alarma" -> Icons.Default.Build
    else -> Icons.Default.Info
}

@Composable
fun AlertaCard(
    alerta: Alerta,
    onAtender: (Int) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val colorFondo by animateColorAsState(
        targetValue = if (alerta.estado == "Activa") {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = tween(500),
        label = "ColorEstadoAlerta"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(iconoDeTipo(alerta.tipo), contentDescription = null, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = alerta.tipo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(alerta.fechaHora)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(alerta.estado, style = MaterialTheme.typography.labelLarge)
            }

            Text("Descripción: ${alerta.descripcion}", modifier = Modifier.padding(top = 8.dp))
            Text("Ubicación: ${alerta.ubicacion}")
            Text("Reportada por: ${alerta.reportadaPor}")

            if (!alerta.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = alerta.fotoUri,
                    contentDescription = "Foto de la alerta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (alerta.estado == "Activa") {
                    Button(onClick = { onAtender(alerta.id) }) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Atendida")
                    }
                }
                OutlinedButton(onClick = { mostrarDialogo = true }) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar")
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar esta alerta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(alerta.id)
                        mostrarDialogo = false
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }
}