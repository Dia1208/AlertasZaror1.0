package cl.zaror.alertas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Query("SELECT * FROM alertas ORDER BY fechaHora DESC")
    fun observarTodas(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas WHERE usuarioId = :usuarioId ORDER BY fechaHora DESC")
    fun observarDeUsuario(usuarioId: Int): Flow<List<AlertaEntity>>

    @Insert
    suspend fun insertar(alerta: AlertaEntity)

    @Query(
        "UPDATE alertas SET estado = :nuevoEstado WHERE id = :id"
    )
    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    )

    /** Cancela solo si la alerta es del usuario y sigue activa. Devuelve filas afectadas. */
    @Query(
        "UPDATE alertas SET estado = 'Cancelada' " +
                "WHERE id = :id AND usuarioId = :usuarioId AND estado = 'Activa'"
    )
    suspend fun cancelarPropia(id: Int, usuarioId: Int): Int

    @Query("DELETE FROM alertas WHERE id = :id")
    suspend fun eliminar(id: Int)
}
