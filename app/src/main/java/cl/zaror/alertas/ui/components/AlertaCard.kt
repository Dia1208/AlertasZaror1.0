package cl.zaror.alertas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cl.zaror.alertas.data.Alerta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertaCard(
    alerta: Alerta,
    onAtender: (Int) -> Unit,
    onEliminar: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = alerta.tipo,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Descripción: ${alerta.descripcion}")
            Text("Ubicación: ${alerta.ubicacion}")
            Text("Reportada por: ${alerta.reportadaPor}")
            Text("Estado: ${alerta.estado}")

            Text(
                text = "Fecha: ${
                    SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date(alerta.fechaHora))
                }",
                modifier = Modifier.padding(top = 4.dp)
            )

            if (!alerta.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = alerta.fotoUri,
                    contentDescription = "Foto de la alerta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            if (alerta.estado == "Activa") {
                Button(
                    onClick = {
                        onAtender(alerta.id)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Marcar como atendida")
                }
            }

            Button(
                onClick = {
                    onEliminar(alerta.id)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Eliminar alerta")
            }
        }
    }
}
