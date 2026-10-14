package cl.zaror.alertas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.zaror.alertas.domain.Validaciones
import cl.zaror.alertas.domain.ValidacionesFalla
import cl.zaror.alertas.ui.components.CampoTexto
import cl.zaror.alertas.ui.components.SelectorChips

@Composable
fun ReporteFallaScreen(
    onRegistrar: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current

    var tipoFalla by remember { mutableStateOf("") }
    var equipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var reportadaPor by remember { mutableStateOf("") }

    var errorTipoFalla by remember { mutableStateOf<String?>(null) }
    var errorEquipo by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var errorReportante by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Reporte de falla del sistema", style = MaterialTheme.typography.headlineSmall)

        SelectorChips(
            titulo = "Tipo de falla",
            opciones = listOf(
                "Sirena sin sonido",
                "Batería baja",
                "Sin energía eléctrica",
                "Botón dañado"
            ),
            seleccionado = tipoFalla,
            error = errorTipoFalla
        ) { opcion ->
            tipoFalla = opcion
            errorTipoFalla = ValidacionesFalla.validarTipoFalla(opcion)
        }

        CampoTexto(
            valor = equipo,
            onCambio = { equipo = it; errorEquipo = null },
            etiqueta = "Equipo o sector afectado",
            icono = Icons.Default.Build,
            error = errorEquipo
        )

        CampoTexto(
            valor = descripcion,
            onCambio = { descripcion = it; errorDescripcion = null },
            etiqueta = "Descripción",
            icono = Icons.Default.Edit,
            error = errorDescripcion,
            minLineas = 3
        )

        CampoTexto(
            valor = reportadaPor,
            onCambio = { reportadaPor = it; errorReportante = null },
            etiqueta = "Reportada por",
            icono = Icons.Default.Person,
            error = errorReportante
        )

        Button(
            onClick = {
                errorTipoFalla = ValidacionesFalla.validarTipoFalla(tipoFalla)
                errorEquipo = ValidacionesFalla.validarEquipo(equipo)
                errorDescripcion = Validaciones.validarDescripcion(descripcion)
                errorReportante = Validaciones.validarReportante(reportadaPor)

                val formularioValido =
                    errorTipoFalla == null &&
                    errorEquipo == null &&
                    errorDescripcion == null &&
                    errorReportante == null

                if (formularioValido) {
                    vibrarAlRegistrar(context)
                    onRegistrar(
                        tipoFalla.trim(),
                        descripcion.trim(),
                        equipo.trim(),
                        reportadaPor.trim()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(Modifier.width(8.dp))
            Text("Registrar falla")
        }
    }
}
