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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
fun RegistroScreen(
    error: String?,
    procesando: Boolean,
    onRegistrar: (nombre: String, telefono: String, direccion: String, clave: String) -> Unit,
    onVolverALogin: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorDireccion by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }
    var errorConfirmacion by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Tus datos identificarán las alarmas que actives",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        CampoTexto(
            valor = nombre,
            onCambio = {
                nombre = it
                errorNombre = null
                onLimpiarError()
            },
            etiqueta = "Nombre completo",
            icono = Icons.Default.Person,
            error = errorNombre
        )

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
            valor = direccion,
            onCambio = {
                direccion = it
                errorDireccion = null
                onLimpiarError()
            },
            etiqueta = "Dirección",
            icono = Icons.Default.Home,
            error = errorDireccion
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

        CampoTexto(
            valor = confirmacion,
            onCambio = {
                confirmacion = it
                errorConfirmacion = null
                onLimpiarError()
            },
            etiqueta = "Confirmar contraseña",
            icono = Icons.Default.Lock,
            error = errorConfirmacion,
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
                errorNombre = ValidacionesUsuario.validarNombre(nombre)
                errorTelefono = ValidacionesUsuario.validarTelefono(telefono)
                errorDireccion = ValidacionesUsuario.validarDireccion(direccion)
                errorClave = ValidacionesUsuario.validarClave(clave)
                errorConfirmacion = ValidacionesUsuario.validarConfirmacion(clave, confirmacion)

                val sinErrores = listOf(
                    errorNombre,
                    errorTelefono,
                    errorDireccion,
                    errorClave,
                    errorConfirmacion
                ).all { it == null }

                if (sinErrores) {
                    onRegistrar(nombre, telefono, direccion, clave)
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
                Text("Crear cuenta")
            }
        }

        TextButton(onClick = onVolverALogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}