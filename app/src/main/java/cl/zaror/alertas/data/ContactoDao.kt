package cl.zaror.alertas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos WHERE usuarioId = :usuarioId ORDER BY nombre COLLATE NOCASE")
    fun observarDeUsuario(usuarioId: Int): Flow<List<ContactoEntity>>

    /** Devuelve -1 si el usuario ya tenía ese teléfono como contacto. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(contacto: ContactoEntity): Long

    /** Solo elimina si el contacto pertenece al usuario indicado. */
    @Query("DELETE FROM contactos WHERE id = :id AND usuarioId = :usuarioId")
    suspend fun eliminar(id: Int, usuarioId: Int)
}
