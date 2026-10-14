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
import cl.zaror.alertas.ui.components.AlertaCard
import cl.zaror.alertas.ui.screens.NuevaAlertaScreen
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
        Column(Modifier.padding(padding)) {
            when (pantalla) {
                "Inicio" -> Text(
                    "AlertasZaror1.0",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(24.dp)
                )

                "Nueva" -> NuevaAlertaScreen {
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

                "Alertas" -> LazyColumn(
                    Modifier.fillMaxWidth().padding(16.dp)
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
