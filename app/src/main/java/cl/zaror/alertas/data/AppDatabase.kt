package cl.zaror.alertas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AlertaEntity::class, UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alertaDao(): AlertaDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Crea la tabla de usuarios sin borrar las alertas existentes.
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `usuarios` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`nombre` TEXT NOT NULL, " +
                            "`telefono` TEXT NOT NULL, " +
                            "`direccion` TEXT NOT NULL, " +
                            "`claveHash` TEXT NOT NULL, " +
                            "`sal` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_telefono` " +
                            "ON `usuarios` (`telefono`)"
                )
            }
        }

        fun obtener(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val nuevaBase = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alertas_zaror.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()

                INSTANCE = nuevaBase
                nuevaBase
            }
        }
    }
}