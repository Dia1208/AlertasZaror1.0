package cl.zaror.alertas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE telefono = :telefono LIMIT 1")
    suspend fun buscarPorTelefono(telefono: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): UsuarioEntity?

    @Query(
        "UPDATE usuarios SET intentosFallidos = :intentos, bloqueadoHasta = :bloqueadoHasta " +
                "WHERE id = :id"
    )
    suspend fun actualizarIntentos(id: Int, intentos: Int, bloqueadoHasta: Long)

    @Query(
        "UPDATE usuarios SET claveHash = :claveHash, sal = :sal, versionHash = :version " +
                "WHERE id = :id"
    )
    suspend fun actualizarHash(id: Int, claveHash: String, sal: String, version: Int)
}
