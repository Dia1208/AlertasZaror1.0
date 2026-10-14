package cl.zaror.alertas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertas")
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,
    val descripcion: String,
    val ubicacion: String,
    val fotoUri: String? = null,
    val estado: String = "Activa",
    val fechaHora: Long = System.currentTimeMillis(),
    val reportadaPor: String
)
