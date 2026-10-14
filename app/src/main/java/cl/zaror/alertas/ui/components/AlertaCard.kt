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

            Text(text = "Descripción: ${alerta.descripcion}")
            Text(text = "Ubicación: ${alerta.ubicacion}")
            Text(text = "Reportada por: ${alerta.reportadaPor}")
            Text(text = "Estado: ${alerta.estado}")

            Text(
                text = "Fecha: ${
                    SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date(alerta.fechaHora))
                }"
            )

            if (!alerta.fotoUri.isNullOrBlank()) {
                Text(
                    text = "Esta alerta tiene una foto adjunta.",
                    modifier = Modifier.padding(top = 8.dp)
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