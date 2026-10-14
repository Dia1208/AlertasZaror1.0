package cl.zaror.alertas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cl.zaror.alertas.data.*
import cl.zaror.alertas.ui.screens.NuevaAlertaScreen
import cl.zaror.alertas.ui.theme.AlertasZaror10Theme
import cl.zaror.alertas.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val db = AppDatabase.obtener(this)
        val repository = AlertasRepository(db.alertaDao())
        val factory = AlertasViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)
            .get(AlertasViewModel::class.java)

        setContent {
            AlertasZaror10Theme {
                AlertasApp(viewModel)
            }
        }
    }
}

@Composable
fun AlertasApp(viewModel: AlertasViewModel) {
    var pantalla by remember { mutableStateOf("Inicio") }
    val alertas by viewModel.alertas.collectAsState()

    Scaffold(
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button({ pantalla = "Inicio" }) { Text("Inicio") }
                Button({ pantalla = "Alertas" }) { Text("Alertas") }
                Button({ pantalla = "Nueva" }) { Text("Nueva") }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (pantalla) {
                "Inicio" -> PantallaInicio()

                "Alertas" -> PantallaAlertas(
                    alertas = alertas,
                    onAtender = viewModel::marcarComoAtendida,
                    onEliminar = viewModel::eliminarAlerta
                )

                "Nueva" -> NuevaAlertaScreen(
                    onRegistrar = {
                            tipo,
                            descripcion,
                            ubicacion,
                            reportadaPor,
                            fotoUri ->

                        viewModel.guardarAlerta(
                            Alerta(
                                tipo = tipo,
                                descripcion = descripcion,
                                ubicacion = ubicacion,
                                reportadaPor = reportadaPor,
                                fotoUri = fotoUri
                            )
                        )

                        pantalla = "Alertas"
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaInicio() {
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "AlertasZaror1.0",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            "Sistema comunitario de alertas para la Junta de Vecinos Población Zaror.",
            Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun PantallaAlertas(
    alertas: List<Alerta>,
    onAtender: (Int) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var filtro by remember { mutableStateOf("Todas") }
    var busqueda by remember { mutableStateOf("") }
    var eliminar by remember { mutableStateOf<Alerta?>(null) }

    val lista = alertas
        .filter {
            filtro == "Todas" || it.estado == filtro.removeSuffix("s")
        }
        .filter {
            val texto = busqueda.lowercase()
            texto.isBlank() ||
                it.tipo.lowercase().contains(texto) ||
                it.descripcion.lowercase().contains(texto) ||
                it.ubicacion.lowercase().contains(texto)
        }

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "Alertas registradas",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar alerta") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button({ filtro = "Todas" }) { Text("Todas") }
            Button({ filtro = "Activas" }) { Text("Activas") }
            Button({ filtro = "Atendidas" }) { Text("Atendidas") }
        }

        LazyColumn {
            items(lista, key = { it.id }) { alerta ->
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(alerta.tipo, style = MaterialTheme.typography.titleMedium)
                    Text("Descripción: ${alerta.descripcion}")
                    Text("Ubicación: ${alerta.ubicacion}")
                    Text("Reportada por: ${alerta.reportadaPor}")
                    Text("Estado: ${alerta.estado}")
                    Text("Registrada: ${fecha(alerta.fechaHora)}")

                    if (alerta.fotoUri != null) {
                        Text("Foto adjunta")
                    }

                    if (alerta.estado == "Activa") {
                        Button(
                            { onAtender(alerta.id) },
                            Modifier.padding(top = 8.dp)
                        ) {
                            Text("Marcar como atendida")
                        }
                    }

                    Button(
                        { eliminar = alerta },
                        Modifier.padding(top = 8.dp)
                    ) {
                        Text("Eliminar alerta")
                    }
                }
            }
        }
    }

    eliminar?.let { alerta ->
        AlertDialog(
            onDismissRequest = { eliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar esta alerta?") },
            confirmButton = {
                TextButton({
                    onEliminar(alerta.id)
                    eliminar = null
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton({ eliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun fecha(valor: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(valor))
