package cl.zaror.alertas.data

data class Contacto(
    val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val telefono: String
)
