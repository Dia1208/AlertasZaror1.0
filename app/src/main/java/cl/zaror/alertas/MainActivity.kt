package cl.zaror.alertas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cl.zaror.alertas.data.Alerta
import cl.zaror.alertas.data.AlertasRepository
import cl.zaror.alertas.data.AppDatabase
import cl.zaror.alertas.ui.screens.NuevaAlertaScreen
import cl.zaror.alertas.ui.theme.AlertasZaror10Theme
import cl.zaror.alertas.viewmodel.AlertasViewModel
import cl.zaror.alertas.viewmodel.AlertasViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseDatos = AppDatabase.obtener(this)
        val repositorio = AlertasRepository(baseDatos.alertaDao())
        val factory = AlertasViewModelFactory(repositorio)

        val viewModel = ViewModelProvider(
            this,
            factory
        )[AlertasViewModel::class.java]

        setContent {
            AlertasZaror10Theme {
                AlertasApp(viewModel)
            }
        }
    }
}

@Composable
fun AlertasApp(viewModel: AlertasViewModel) {
    var pantallaActual by remember {
        mutableStateOf("Inicio")
    }

    val alertas by viewModel.alertas.collectAsState()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonNavegacion(
                    texto = "Inicio",
                    seleccionado = pantallaActual == "Inicio"
                ) {
                    pantallaActual = "Inicio"
                }

                BotonNavegacion(
                    texto = "Alertas",
                    seleccionado = pantallaActual == "Alertas"
                ) {
                    pantallaActual = "Alertas"
                }

                BotonNavegacion(
                    texto = "Nueva",
                    seleccionado = pantallaActual == "Nueva"
                ) {
                    pantallaActual = "Nueva"
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (pantallaActual) {
                "Inicio" -> PantallaInicio()

                "Alertas" -> PantallaAlertas(
                    alertas = alertas,
                    onAtender = { id ->
                        viewModel.marcarComoAtendida(id)
                    },
                    onEliminar = { id ->
                        viewModel.eliminarAlerta(id)
                    }
                )

                "Nueva" -> NuevaAlertaScreen(
                    onRegistrar = {
                            tipo,
                            descripcion,
                            ubicacion,
                            reportadaPor ->

                        val nuevaAlerta = Alerta(
                            tipo = tipo,
                            descripcion = descripcion,
                            ubicacion = ubicacion,
                            reportadaPor = reportadaPor
                        )

                        viewModel.guardarAlerta(nuevaAlerta)
                        pantallaActual = "Alertas"
                    }
                )
            }
        }
    }
}

@Composable
fun BotonNavegacion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(
            text = if (seleccionado) {
                "● $texto"
            } else {
                texto
            }
        )
    }
}

@Composable
fun PantallaInicio() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AlertasZaror1.0",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Sistema comunitario de alertas para la Junta de Vecinos Población Zaror.",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun PantallaAlertas(
    alertas: List<Alerta>,
    onAtender: (Int) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var alertaPendienteEliminar by remember {
        mutableStateOf<Alerta?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Alertas registradas",
            style = MaterialTheme.typography.headlineMedium
        )

        if (alertas.isEmpty()) {
            Text(
                text = "Todavía no hay alertas registradas.",
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(
                    items = alertas,
                    key = { alerta -> alerta.id }
                ) { alerta ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = alerta.tipo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Descripción: ${alerta.descripcion}"
                        )

                        Text(
                            text = "Ubicación: ${alerta.ubicacion}"
                        )

                        Text(
                            text = "Reportada por: ${alerta.reportadaPor}"
                        )

                        Text(
                            text = "Estado: ${alerta.estado}"
                        )

                        Text(
                            text = "Registrada: ${
                                formatearFechaHora(alerta.fechaHora)
                            }"
                        )

                        if (alerta.estado == "Activa") {
                            Button(
                                onClick = {
                                    onAtender(alerta.id)
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Marcar como atendida")
                            }
                        }

                        Button(
                            onClick = {
                                alertaPendienteEliminar = alerta
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Eliminar alerta")
                        }
                    }
                }
            }
        }
    }

    alertaPendienteEliminar?.let { alerta ->
        AlertDialog(
            onDismissRequest = {
                alertaPendienteEliminar = null
            },
            title = {
                Text("Confirmar eliminación")
            },
            text = {
                Text("¿Seguro que deseas eliminar esta alerta?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(alerta.id)
                        alertaPendienteEliminar = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        alertaPendienteEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun formatearFechaHora(fechaHora: Long): String {
    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formato.format(Date(fechaHora))
}
