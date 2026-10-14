package cl.zaror.alertas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Query("SELECT * FROM alertas ORDER BY fechaHora DESC")
    fun observarTodas(): Flow<List<AlertaEntity>>

    @Insert
    suspend fun insertar(alerta: AlertaEntity)

    @Query(
        "UPDATE alertas SET estado = :nuevoEstado WHERE id = :id"
    )
    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    )

    @Query("DELETE FROM alertas WHERE id = :id")
    suspend fun eliminar(id: Int)
}
