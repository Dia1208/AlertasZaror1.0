package cl.zaror.alertas.ui.screens

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.domain.Validaciones

@Composable
fun NuevaAlertaScreen(
    onRegistrar: (
        String,
        String,
        String,
        String,
        String?
    ) -> Unit
) {
    val context = LocalContext.current

    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var reportadaPor by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }

    var errorTipo by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var errorUbicacion by remember { mutableStateOf<String?>(null) }
    var errorReportante by remember { mutableStateOf<String?>(null) }

    val selectorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        fotoUri = uri?.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Registrar nueva alerta",
            style = MaterialTheme.typography.headlineSmall
        )

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
                        errorTipo = Validaciones.validarTipo(tipo)
                    }
                )
                Text(
                    text = opcion,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        errorTipo?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                errorDescripcion = null
            },
            label = { Text("Descripción") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            trailingIcon = { if (errorDescripcion != null) Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) },
            isError = errorDescripcion != null,
            supportingText = {
                errorDescripcion?.let { Text(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = {
                ubicacion = it
                errorUbicacion = null
            },
            label = { Text("Ubicación de prueba") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            trailingIcon = { if (errorUbicacion != null) Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) },
            isError = errorUbicacion != null,
            supportingText = {
                errorUbicacion?.let { Text(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedButton(
            onClick = {
                ubicacion = "Ubicación ficticia: Block 3, casa 15"
                errorUbicacion = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Usar ubicación ficticia")
        }

        OutlinedButton(
            onClick = {
                probarVibracion(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Probar vibración del dispositivo")
        }

        OutlinedTextField(
            value = reportadaPor,
            onValueChange = {
                reportadaPor = it
                errorReportante = null
            },
            label = { Text("Reportada por") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { if (errorReportante != null) Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) },
            isError = errorReportante != null,
            supportingText = {
                errorReportante?.let { Text(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedButton(
            onClick = {
                selectorFoto.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                if (fotoUri == null) {
                    "Adjuntar foto opcional"
                } else {
                    "Foto seleccionada"
                }
            )
        }

        Button(
            onClick = {
                errorTipo = Validaciones.validarTipo(tipo)
                errorDescripcion =
                    Validaciones.validarDescripcion(descripcion)
                errorUbicacion =
                    Validaciones.validarUbicacion(ubicacion)
                errorReportante =
                    Validaciones.validarReportante(reportadaPor)

                val formularioValido =
                    errorTipo == null &&
                    errorDescripcion == null &&
                    errorUbicacion == null &&
                    errorReportante == null

                if (formularioValido) {
                    vibrarAlRegistrar(context)
                    onRegistrar(
                        tipo.trim(),
                        descripcion.trim(),
                        ubicacion.trim(),
                        reportadaPor.trim(),
                        fotoUri
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text("Registrar alerta")
        }
    }
}

private fun vibrarAlRegistrar(context: Context) {
    val vibrador = context.getSystemService(Vibrator::class.java)
    if (vibrador?.hasVibrator() == true) {
        vibrador.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

private fun probarVibracion(context: Context) {
    val vibrador = context.getSystemService(Vibrator::class.java)

    if (vibrador?.hasVibrator() == true) {
        vibrador.vibrate(
            VibrationEffect.createOneShot(
                250,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
        Toast.makeText(
            context,
            "Prueba de vibración ejecutada",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        Toast.makeText(
            context,
            "Este dispositivo no dispone de vibración",
            Toast.LENGTH_SHORT
        ).show()
    }
}