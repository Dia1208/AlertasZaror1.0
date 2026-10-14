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
    var pantallaActual by remember { mutableStateOf("Inicio") }
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
                    onClick = { pantallaActual = "Inicio" }
                )

                BotonNavegacion(
                    texto = "Alertas",
                    seleccionado = pantallaActual == "Alertas",
                    onClick = { pantallaActual = "Alertas" }
                )

                BotonNavegacion(
                    texto = "Nueva",
                    seleccionado = pantallaActual == "Nueva",
                    onClick = { pantallaActual = "Nueva" }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (pantallaActual) {
                "Inicio" -> PantallaInicio()

                "Alertas" -> PantallaAlertas(
                    cantidadAlertas = alertas.size
                )

                "Nueva" -> NuevaAlertaScreen(
                    onRegistrar = { tipo, descripcion, ubicacion, reportadaPor ->

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
            text = if (seleccionado) "● $texto" else texto
        )
    }
}

@Composable
fun PantallaInicio() {
    Text(
        text = "AlertasZaror1.0",
        style = MaterialTheme.typography.headlineMedium
    )

    Text(
        text = "Sistema comunitario de alertas para la Junta de Vecinos Población Zaror.",
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
fun PantallaAlertas(cantidadAlertas: Int) {
    Text(
        text = "Alertas registradas",
        style = MaterialTheme.typography.headlineMedium
    )

    Text(
        text = "Cantidad de alertas guardadas: $cantidadAlertas",
        modifier = Modifier.padding(top = 12.dp)
    )
}