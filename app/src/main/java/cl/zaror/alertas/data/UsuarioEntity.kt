package cl.zaror.alertas.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["telefono"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val claveHash: String,
    val sal: String,
    /** Versión del algoritmo de hash. Las cuentas antiguas quedan en 1. */
    @ColumnInfo(defaultValue = "1")
    val versionHash: Int = 1,
    @ColumnInfo(defaultValue = "0")
    val intentosFallidos: Int = 0,
    /** Instante (ms) hasta el que el login está bloqueado; 0 si no lo está. */
    @ColumnInfo(defaultValue = "0")
    val bloqueadoHasta: Long = 0L
)
