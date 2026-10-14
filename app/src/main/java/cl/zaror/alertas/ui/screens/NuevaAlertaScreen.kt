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

    var errorTipo by remember { mutableStateOf("") }
    var errorDescripcion by remember { mutableStateOf("") }
    var errorUbicacion by remember { mutableStateOf("") }
    var errorReportadaPor by remember { mutableStateOf("") }

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
                    onClick = {
                        tipo = opcion
                        errorTipo = ""
                    }
                )

                Text(
                    text = opcion,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        if (errorTipo.isNotEmpty()) {
            Text(text = errorTipo)
        }

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                errorDescripcion = ""
            },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            isError = errorDescripcion.isNotEmpty()
        )

        if (errorDescripcion.isNotEmpty()) {
            Text(text = errorDescripcion)
        }

        OutlinedTextField(
            value = ubicacion,
            onValueChange = {
                ubicacion = it
                errorUbicacion = ""
            },
            label = { Text("Ubicación aproximada") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            isError = errorUbicacion.isNotEmpty()
        )

        if (errorUbicacion.isNotEmpty()) {
            Text(text = errorUbicacion)
        }

        OutlinedTextField(
            value = reportadaPor,
            onValueChange = {
                reportadaPor = it
                errorReportadaPor = ""
            },
            label = { Text("Reportada por") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            isError = errorReportadaPor.isNotEmpty()
        )

        if (errorReportadaPor.isNotEmpty()) {
            Text(text = errorReportadaPor)
        }

        Button(
            onClick = {
                errorTipo =
                    if (tipo.isBlank()) "Selecciona un tipo de alerta" else ""

                errorDescripcion =
                    if (descripcion.isBlank()) "Ingresa una descripción" else ""

                errorUbicacion =
                    if (ubicacion.isBlank()) "Ingresa una ubicación" else ""

                errorReportadaPor =
                    if (reportadaPor.isBlank()) "Ingresa quién reporta la alerta" else ""

                val formularioValido =
                    tipo.isNotBlank() &&
                            descripcion.isNotBlank() &&
                            ubicacion.isNotBlank() &&
                            reportadaPor.isNotBlank()

                if (formularioValido) {
                    onRegistrar(
                        tipo.trim(),
                        descripcion.trim(),
                        ubicacion.trim(),
                        reportadaPor.trim()
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(text = "Registrar alerta")
        }
    }
}