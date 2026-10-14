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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseDatos = AppDatabase.obtener(this)
        val repositorio = AlertasRepository(baseDatos.alertaDao())
        val factory = AlertasViewModelFactory(repositorio)

        val alertasViewModel = ViewModelProvider(
            this,
            factory
        )[AlertasViewModel::class.java]

        setContent {
            AlertasZaror10Theme {
                AlertasApp(alertasViewModel)
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
                    seleccionado = pantallaActual == "Inicio",
                    onClick = {
                        pantallaActual = "Inicio"
                    }
                )

                BotonNavegacion(
                    texto = "Alertas",
                    seleccionado = pantallaActual == "Alertas",
                    onClick = {
                        pantallaActual = "Alertas"
                    }
                )

                BotonNavegacion(
                    texto = "Nueva",
                    seleccionado = pantallaActual == "Nueva",
                    onClick = {
                        pantallaActual = "Nueva"
                    }
                )
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
                "Inicio" -> {
                    PantallaInicio()
                }

                "Alertas" -> {
                    PantallaAlertas(
                        alertas = alertas,
                        onAtender = { id ->
                            viewModel.marcarComoAtendida(id)
                        }
                    )
                }

                "Nueva" -> {
                    NuevaAlertaScreen(
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
    onAtender: (Int) -> Unit
) {
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
            alertas.forEach { alerta ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = alerta.tipo,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Descripción: ${alerta.descripcion}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Ubicación: ${alerta.ubicacion}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Reportada por: ${alerta.reportadaPor}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Estado: ${alerta.estado}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (alerta.estado == "Activa") {
                        Button(
                            onClick = {
                                onAtender(alerta.id)
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(text = "Marcar como atendida")
                        }
                    }
                }
            }
        }
    }
}