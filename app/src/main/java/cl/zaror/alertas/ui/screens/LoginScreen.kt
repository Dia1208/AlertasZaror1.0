package cl.zaror.alertas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.domain.ValidacionesUsuario
import cl.zaror.alertas.ui.components.CampoTexto

@Composable
fun LoginScreen(
    error: String?,
    procesando: Boolean,
    onIniciarSesion: (telefono: String, clave: String) -> Unit,
    onIrARegistro: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var telefono by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "Alertas Zaror",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = "Junta de Vecinos Población Zaror",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        CampoTexto(
            valor = telefono,
            onCambio = {
                telefono = it
                errorTelefono = null
                onLimpiarError()
            },
            etiqueta = "Teléfono",
            icono = Icons.Default.Phone,
            error = errorTelefono,
            teclado = KeyboardType.Phone
        )

        CampoTexto(
            valor = clave,
            onCambio = {
                clave = it
                errorClave = null
                onLimpiarError()
            },
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock,
            error = errorClave,
            esClave = true
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                errorTelefono = ValidacionesUsuario.validarTelefono(telefono)
                errorClave = if (clave.isBlank()) "Ingresa tu contraseña" else null

                if (errorTelefono == null && errorClave == null) {
                    onIniciarSesion(telefono.trim(), clave)
                }
            },
            enabled = !procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (procesando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}