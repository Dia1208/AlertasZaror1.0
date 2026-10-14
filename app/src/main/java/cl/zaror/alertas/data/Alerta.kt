package cl.zaror.alertas.data

data class Alerta(
    val id: Int = 0,
    val tipo: String,
    val descripcion: String,
    val ubicacion: String,
    val fotoUri: String? = null,
    val estado: String = "Activa",
    val fechaHora: Long = System.currentTimeMillis(),
    val reportadaPor: String
)
