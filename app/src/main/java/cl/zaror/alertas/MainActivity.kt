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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.ui.theme.AlertasZaror10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlertasZaror10Theme {
                AlertasApp()
            }
        }
    }
}

@Composable
fun AlertasApp() {
    var pantallaActual by remember { mutableStateOf("Inicio") }

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
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (pantallaActual) {
                "Inicio" -> PantallaInicio()
                "Alertas" -> PantallaAlertas()
                "Nueva" -> PantallaNuevaAlerta()
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
fun PantallaAlertas() {
    Text(
        text = "Alertas registradas",
        style = MaterialTheme.typography.headlineMedium
    )

    Text(
        text = "Aquí aparecerá el historial de alertas.",
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
fun PantallaNuevaAlerta() {
    Text(
        text = "Nueva alerta",
        style = MaterialTheme.typography.headlineMedium
    )

    Text(
        text = "Aquí construiremos el formulario para registrar una alerta.",
        modifier = Modifier.padding(top = 12.dp)
    )
}