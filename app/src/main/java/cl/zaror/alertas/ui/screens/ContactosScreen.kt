package cl.zaror.alertas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cl.zaror.alertas.data.Contacto
import cl.zaror.alertas.domain.ValidacionesContacto
import cl.zaror.alertas.ui.components.CampoTexto

/** Lista y formulario de contactos de confianza. Sin límite de contactos. */
@Composable
fun ContactosScreen(
    contactos: List<Contacto>,
    error: String?,
    onAgregar: (nombre: String, telefono: String, alExito: () -> Unit) -> Unit,
    onEliminar: (Int) -> Unit,
    onLimpiarError: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var contactoAEliminar by remember { mutableStateOf<Contacto?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Contactos de confianza", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Personas a las que se avisará cuando actives una alarma",
                    style = MaterialTheme.typography.bodyMedium
                )

                CampoTexto(
                    valor = nombre,
                    onCambio = {
                        nombre = it
                        errorNombre = null
                        onLimpiarError()
                    },
                    etiqueta = "Nombre",
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
                    etiqueta = "Teléfono (ej. 912345678)",
                    icono = Icons.Default.Phone,
                    error = errorTelefono,
                    teclado = KeyboardType.Phone
                )

                if (error != null) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        errorNombre = ValidacionesContacto.validarNombre(nombre)
                        errorTelefono = ValidacionesContacto.validarTelefono(telefono)
                        if (errorNombre == null && errorTelefono == null) {
                            onAgregar(nombre, telefono) {
                                nombre = ""
                                telefono = ""
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar contacto")
                }
            }
        }

        if (contactos.isEmpty()) {
            item {
                Text(
                    "Aún no tienes contactos de confianza.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            items(contactos, key = { it.id }) { contacto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(contacto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(contacto.telefono, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { contactoAEliminar = contacto }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar contacto")
                        }
                    }
                }
            }
        }
    }

    contactoAEliminar?.let { contacto ->
        AlertDialog(
            onDismissRequest = { contactoAEliminar = null },
            title = { Text("Eliminar contacto") },
            text = { Text("¿Quitar a ${contacto.nombre} de tus contactos de confianza?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(contacto.id)
                        contactoAEliminar = null
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { contactoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
