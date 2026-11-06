package cl.zaror.alertas.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contactos",
    indices = [Index(value = ["usuarioId", "telefono"], unique = true)]
)
data class ContactoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val telefono: String
)
