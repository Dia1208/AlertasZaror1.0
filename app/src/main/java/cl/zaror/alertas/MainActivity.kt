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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cl.zaror.alertas.data.*
import cl.zaror.alertas.domain.Permisos
import cl.zaror.alertas.domain.Rol
import cl.zaror.alertas.ui.components.AlertaCard
import cl.zaror.alertas.ui.screens.ContactosScreen
import cl.zaror.alertas.ui.screens.LoginScreen
import cl.zaror.alertas.ui.screens.NuevaAlertaScreen
import cl.zaror.alertas.ui.screens.RegistroScreen
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

        val contactosFactory = ContactosViewModelFactory(
            ContactosRepository(db.contactoDao())
        )
        val contactosViewModel = ViewModelProvider(
            this,
            contactosFactory
        )[ContactosViewModel::class.java]

        val usuarioDao = db.usuarioDao()
        val sesionManager = SesionManager(this)

        val authFactory = AuthViewModelFactory(
            UsuariosRepository(usuarioDao),
            sesionManager,
            usuarioDao,
            cl.zaror.alertas.data.remote.RetrofitClient.authApi,
            TokenManager(this)
        )
        val authViewModel = ViewModelProvider(
            this,
            authFactory
        )[AuthViewModel::class.java]

        setContent {
            AlertasZaror10Theme {
                AccesoApp(authViewModel, viewModel, contactosViewModel)
            }
        }
    }
}

/** Decide qué mostrar según la sesión: carga, login/registro o la app. */
@Composable
fun AccesoApp(
    authViewModel: AuthViewModel,
    viewModel: AlertasViewModel,
    contactosViewModel: ContactosViewModel
) {
    val estado by authViewModel.estadoSesion.collectAsState()
    val error by authViewModel.error.collectAsState()
    val procesando by authViewModel.procesando.collectAsState()
    var mostrarRegistro by remember { mutableStateOf(false) }

    when (val sesion = estado) {
        EstadoSesion.Cargando -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        EstadoSesion.SinSesion -> if (mostrarRegistro) {
            RegistroScreen(
                error = error,
                procesando = procesando,
                onRegistrar = authViewModel::registrar,
                onVolverALogin = {
                    authViewModel.limpiarError()
                    mostrarRegistro = false
                },
                onLimpiarError = authViewModel::limpiarError
            )
        } else {
            LoginScreen(
                error = error,
                procesando = procesando,
                onIniciarSesion = authViewModel::iniciarSesion,
                onIrARegistro = {
                    authViewModel.limpiarError()
                    mostrarRegistro = true
                },
                onLimpiarError = authViewModel::limpiarError
            )
        }

        is EstadoSesion.Activa -> AlertasApp(
            viewModel = viewModel,
            contactosViewModel = contactosViewModel,
            usuario = sesion.usuario,
            onCerrarSesion = authViewModel::cerrarSesion
        )
    }
}

@Composable
fun AlertasApp(
    viewModel: AlertasViewModel,
    contactosViewModel: ContactosViewModel,
    usuario: UsuarioSesion,
    onCerrarSesion: () -> Unit
) {
    var pantalla by remember { mutableStateOf("Inicio") }
    val alertasFlow = remember(usuario) { viewModel.alertasVisibles(usuario) }
    val alertas by alertasFlow.collectAsState(initial = emptyList())
    val contactosFlow = remember(usuario) { contactosViewModel.contactos(usuario) }
    val contactos by contactosFlow.collectAsState(initial = emptyList())
    val errorContacto by contactosViewModel.error.collectAsState()
    val esAdmin = usuario.rol == Rol.ADMINISTRADOR
    val activas = alertas.count { it.estado == "Activa" }

    val pestanas = listOf(
        Triple("Inicio", "Inicio", Icons.Default.Home),
        Triple("Alertas", "Alertas", Icons.Default.Notifications),
        Triple("Nueva", "Nueva", Icons.Default.AddCircle),
        Triple("Falla", "Falla", Icons.Default.Build),
        Triple("Contactos", "Contactos", Icons.Default.Contacts)
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
            if (pantalla != "Nueva" && pantalla != "Falla" && pantalla != "Contactos") {
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
                "Inicio" -> PantallaInicio(
                    usuario = usuario.nombre,
                    esAdmin = esAdmin,
                    activas = activas,
                    total = alertas.size,
                    onCerrarSesion = onCerrarSesion
                )
                "Nueva" -> NuevaAlertaScreen(
                    nombreUsuario = usuario.nombre
                ) { tipo, descripcion, ubicacion, fotoUri ->
                    viewModel.registrarAlerta(
                        usuario = usuario,
                        tipo = tipo,
                        descripcion = descripcion,
                        ubicacion = ubicacion,
                        fotoUri = fotoUri
                    )
                    pantalla = "Alertas"
                }

                "Falla" -> ReporteFallaScreen(
                    nombreUsuario = usuario.nombre
                ) { tipoFalla, descripcion, equipo ->
                    viewModel.registrarAlerta(
                        usuario = usuario,
                        tipo = "Falla de alarma",
                        descripcion = "[$tipoFalla] $descripcion",
                        ubicacion = equipo
                    )
                    pantalla = "Alertas"
                }

                "Contactos" -> ContactosScreen(
                    contactos = contactos,
                    error = errorContacto,
                    onAgregar = { nombre, telefono, alExito ->
                        contactosViewModel.agregar(usuario, nombre, telefono, alExito)
                    },
                    onEliminar = { id -> contactosViewModel.eliminar(usuario, id) },
                    onLimpiarError = contactosViewModel::limpiarError
                )

                "Alertas" -> if (alertas.isEmpty()) {
                    Text(
                        if (esAdmin) {
                            "Aún no hay alertas registradas."
                        } else {
                            "Aún no tienes alertas. Usa «Activar alarma» para registrar una."
                        },
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 96.dp
                        )
                    ) {
                        items(alertas, key = { it.id }) { alerta ->
                            AlertaCard(
                                alerta = alerta,
                                puedeAtender = Permisos.puedeMarcarAtendida(usuario.rol) &&
                                        alerta.estado == Permisos.ESTADO_ACTIVA,
                                puedeCancelar = Permisos.puedeCancelar(
                                    rol = usuario.rol,
                                    usuarioId = usuario.id,
                                    duenoId = alerta.usuarioId,
                                    estado = alerta.estado
                                ),
                                puedeEliminar = Permisos.puedeEliminar(usuario.rol),
                                mostrarReportante = esAdmin,
                                onAtender = { id -> viewModel.marcarComoAtendida(usuario, id) },
                                onCancelar = { id -> viewModel.cancelarAlerta(usuario, id) },
                                onEliminar = { id -> viewModel.eliminarAlerta(usuario, id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Botón con animación de pulso infinita para llamar la atención. */
@Composable
private fun PantallaInicio(
    usuario: String,
    esAdmin: Boolean,
    activas: Int,
    total: Int,
    onCerrarSesion: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val entrada by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 750),
        label = "entradaInicio"
    )

    val contador by animateIntAsState(
        targetValue = activas,
        animationSpec = tween(durationMillis = 650),
        label = "contadorAlertas"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .graphicsLayer {
                alpha = entrada
                translationY = (1f - entrada) * 28f
            }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.padding(15.dp).size(34.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column {
                Text(
                    "Alertas Zaror",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Junta de Vecinos Población Zaror",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (activas > 0) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (activas > 0) "Atención: hay alertas activas"
                        else "Estado de las alertas",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    contador.toString(),
                    style = MaterialTheme.typography.displayLarge
                )

                Text(
                    if (esAdmin) "Alertas activas en la comunidad"
                    else "Alertas activas registradas por ti",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                )

                Text(
                    if (esAdmin) "Total registradas: $total"
                    else "Total de mis alertas: $total",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        "¡Bienvenido!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        usuario,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        if (esAdmin) "Administrador" else "Vecino",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        TextButton(
            onClick = onCerrarSesion,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}
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
