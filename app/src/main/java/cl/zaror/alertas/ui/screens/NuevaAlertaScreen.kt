package cl.zaror.alertas.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

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
    val clienteUbicacion =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var reportadaPor by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }

    var error by remember { mutableStateOf("") }

    val selectorFoto =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            fotoUri = it?.toString()
        }

    val permisoUbicacion =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->
            val permitido =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permitido) {
                clienteUbicacion.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        ubicacion =
                            "${location.latitude}, ${location.longitude}"
                    }
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("Registrar nueva alerta")

        Text(
            "Tipo de alerta",
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
                        error = ""
                    }
                )
                Text(
                    opcion,
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
            label = { Text("Ubicación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedButton(
            onClick = {
                val tienePermiso =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (tienePermiso) {
                    clienteUbicacion.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            ubicacion =
                                "${it.latitude}, ${it.longitude}"
                        }
                    }
                } else {
                    permisoUbicacion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Obtener ubicación actual")
        }

        OutlinedTextField(
            value = reportadaPor,
            onValueChange = { reportadaPor = it },
            label = { Text("Reportada por") },
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

        if (error.isNotEmpty()) {
            Text(
                error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (
                    tipo.isBlank() ||
                    descripcion.isBlank() ||
                    ubicacion.isBlank() ||
                    reportadaPor.isBlank()
                ) {
                    error = "Completa todos los campos obligatorios"
                } else {
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
