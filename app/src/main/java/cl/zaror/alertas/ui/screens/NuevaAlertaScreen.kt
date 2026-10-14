package cl.zaror.alertas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NuevaAlertaScreen(
    onRegistrar: (
        tipo: String,
        descripcion: String,
        ubicacion: String,
        reportadaPor: String
    ) -> Unit
) {
    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var reportadaPor by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(text = "Registrar nueva alerta")

        Text(
            text = "Tipo de alerta",
            modifier = Modifier.padding(top = 16.dp)
        )

        listOf(
            "Emergencia",
            "Falla de alarma",
            "Solicitud de apoyo"
        ).forEach { opcion ->
            Row {
                RadioButton(
                    selected = tipo == opcion,
                    onClick = { tipo = opcion }
                )

                Text(
                    text = opcion,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación aproximada") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedTextField(
            value = reportadaPor,
            onValueChange = { reportadaPor = it },
            label = { Text("Reportada por") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        Button(
            onClick = {
                onRegistrar(
                    tipo,
                    descripcion,
                    ubicacion,
                    reportadaPor
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(text = "Registrar alerta")
        }
    }
}
