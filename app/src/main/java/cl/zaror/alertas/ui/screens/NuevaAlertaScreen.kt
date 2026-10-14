package cl.zaror.alertas.ui.screens

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.domain.Validaciones
import cl.zaror.alertas.ui.components.CampoTexto
import cl.zaror.alertas.ui.components.SelectorChips

@Composable
fun NuevaAlertaScreen(
    onRegistrar: (String, String, String, String, String?) -> Unit
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
    ) { uri -> fotoUri = uri?.toString() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar nueva alerta", style = MaterialTheme.typography.headlineSmall)

        SelectorChips(
            titulo = "Tipo de alerta",
            opciones = listOf("Emergencia", "Falla de alarma", "Solicitud de apoyo"),
            seleccionado = tipo,
            error = errorTipo
        ) { opcion ->
            tipo = opcion
            errorTipo = Validaciones.validarTipo(opcion)
        }

        CampoTexto(
            valor = descripcion,
            onCambio = { descripcion = it; errorDescripcion = null },
            etiqueta = "Descripción",
            icono = Icons.Default.Edit,
            error = errorDescripcion,
            minLineas = 3
        )

        CampoTexto(
            valor = ubicacion,
            onCambio = { ubicacion = it; errorUbicacion = null },
            etiqueta = "Ubicación de prueba",
            icono = Icons.Default.Place,
            error = errorUbicacion
        )

        OutlinedButton(
            onClick = {
                ubicacion = "Ubicación ficticia: Block 3, casa 15"
                errorUbicacion = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Place, null)
            Spacer(Modifier.width(8.dp))
            Text("Usar ubicación ficticia")
        }

        OutlinedButton(
            onClick = { probarVibracion(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.NotificationsActive, null)
            Spacer(Modifier.width(8.dp))
            Text("Probar vibración del dispositivo")
        }

        CampoTexto(
            valor = reportadaPor,
            onCambio = { reportadaPor = it; errorReportante = null },
            etiqueta = "Reportada por",
            icono = Icons.Default.Person,
            error = errorReportante
        )

        OutlinedButton(
            onClick = { selectorFoto.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AddAPhoto, null)
            Spacer(Modifier.width(8.dp))
            Text(if (fotoUri == null) "Adjuntar foto opcional" else "Foto seleccionada")
        }

        Button(
            onClick = {
                errorTipo = Validaciones.validarTipo(tipo)
                errorDescripcion = Validaciones.validarDescripcion(descripcion)
                errorUbicacion = Validaciones.validarUbicacion(ubicacion)
                errorReportante = Validaciones.validarReportante(reportadaPor)

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
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(Modifier.width(8.dp))
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
            VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE)
        )
        Toast.makeText(context, "Prueba de vibración ejecutada", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Este dispositivo no dispone de vibración", Toast.LENGTH_SHORT).show()
    }
}