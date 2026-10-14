package cl.zaror.alertas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

        val database = AppDatabase.obtener(this)
        val repository = AlertasRepository(database.alertaDao())
        val factory = AlertasViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)
            [AlertasViewModel::class.java]

        setContent {
            AlertasZaror10Theme {
                AlertasApp(viewModel)
            }
        }
    }
}

@Composable
fun AlertasApp(viewModel: AlertasViewModel) {
    var screen by remember { mutableStateOf("Inicio") }
    val alertas by viewModel.alertas.collectAsState()

    Scaffold(
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button({ screen = "Inicio" }) { Text("Inicio") }
                Button({ screen = "Alertas" }) { Text("Alertas") }
                Button({ screen = "Nueva" }) { Text("Nueva") }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when (screen) {
                "Inicio" -> Text(
                    "AlertasZaror1.0",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(24.dp)
                )

                "Nueva" -> NuevaAlertaScreen { tipo, descripcion,
                    ubicacion, reportadaPor, fotoUri ->

                    viewModel.guardarAlerta(
                        Alerta(
                            tipo = tipo,
                            descripcion = descripcion,
                            ubicacion = ubicacion,
                            reportadaPor = reportadaPor,
                            fotoUri = fotoUri
                        )
                    )
                    screen = "Alertas"
                }

                "Alertas" -> LazyColumn(
                    Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    items(alertas, key = { it.id }) { alerta ->
                        Column(
                            Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Text(alerta.tipo,
                                style = MaterialTheme.typography.titleMedium)
                            Text("Descripción: ${alerta.descripcion}")
                            Text("Ubicación: ${alerta.ubicacion}")
                            Text("Reportada por: ${alerta.reportadaPor}")
                            Text("Estado: ${alerta.estado}")
                            Text(
                                "Fecha: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy HH:mm",
                                        Locale.getDefault()
                                    ).format(Date(alerta.fechaHora))
                                }"
                            )

                            if (alerta.estado == "Activa") {
                                Button({
                                    viewModel.marcarComoAtendida(alerta.id)
                                }) {
                                    Text("Marcar como atendida")
                                }
                            }

                            Button({
                                viewModel.eliminarAlerta(alerta.id)
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
