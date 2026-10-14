package cl.zaror.alertas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cl.zaror.alertas.data.*
import cl.zaror.alertas.ui.components.AlertaCard
import cl.zaror.alertas.ui.screens.NuevaAlertaScreen
import cl.zaror.alertas.ui.screens.ReporteFallaScreen
import cl.zaror.alertas.ui.theme.AlertasZaror10Theme
import cl.zaror.alertas.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val db = AppDatabase.obtener(this)
        val repository = AlertasRepository(db.alertaDao())
        val factory = AlertasViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[AlertasViewModel::class.java]

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
    val activas = alertas.count { it.estado == "Activa" }

    val pestanas = listOf(
        Triple("Inicio", "Inicio", Icons.Default.Home),
        Triple("Alertas", "Alertas", Icons.Default.Notifications),
        Triple("Nueva", "Nueva", Icons.Default.AddCircle),
        Triple("Falla", "Falla", Icons.Default.Build)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                pestanas.forEach { (ruta, etiqueta, icono) ->
                    NavigationBarItem(
                        selected = pantalla == ruta,
                        onClick = { pantalla = ruta },
                        icon = { Icon(icono, contentDescription = etiqueta) },
                        label = { Text(etiqueta) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (pantalla != "Nueva" && pantalla != "Falla") {
                BotonAlarma { pantalla = "Nueva" }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = pantalla,
            modifier = Modifier.padding(padding),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "AnimacionPantallas"
        ) { pantallaActual ->
            when (pantallaActual) {
                "Inicio" -> Column(Modifier.padding(24.dp)) {
                    Text("Alertas Zaror", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Junta de Vecinos Población Zaror",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (activas > 0) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Alertas activas: $activas", style = MaterialTheme.typography.titleMedium)
                            Text("Total registradas: ${alertas.size}")
                        }
                    }
                }

                "Nueva" -> NuevaAlertaScreen { tipo, descripcion, ubicacion, reportadaPor, fotoUri ->
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

                "Falla" -> ReporteFallaScreen { tipoFalla, descripcion, equipo, reportadaPor ->
                    viewModel.guardarAlerta(
                        Alerta(
                            tipo = "Falla de alarma",
                            descripcion = "[$tipoFalla] $descripcion",
                            ubicacion = equipo,
                            reportadaPor = reportadaPor
                        )
                    )
                    pantalla = "Alertas"
                }

                "Alertas" -> if (alertas.isEmpty()) {
                    Text(
                        "Aún no hay alertas. Usa «Activar alarma» para registrar una.",
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)
                    ) {
                        items(alertas, key = { it.id }) { alerta ->
                            AlertaCard(
                                alerta = alerta,
                                onAtender = viewModel::marcarComoAtendida,
                                onEliminar = viewModel::eliminarAlerta
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Boton con animacion de pulso infinita para llamar la atencion. */
@Composable
private fun BotonAlarma(onClick: () -> Unit) {
    val transicion = rememberInfiniteTransition(label = "pulso")
    val escala by transicion.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "escala"
    )
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = Modifier.graphicsLayer { scaleX = escala; scaleY = escala },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(Icons.Default.NotificationsActive, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Activar alarma")
    }
}