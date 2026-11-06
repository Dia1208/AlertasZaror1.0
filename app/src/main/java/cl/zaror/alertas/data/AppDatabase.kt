package cl.zaror.alertas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AlertaEntity::class, UsuarioEntity::class, ContactoEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alertaDao(): AlertaDao

    abstract fun usuarioDao(): UsuarioDao

    abstract fun contactoDao(): ContactoDao

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

        // Vincula cada alerta con su usuario. Las alertas antiguas quedan con 0.
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `alertas` ADD COLUMN `usuarioId` INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // Crea la tabla de contactos de confianza sin tocar los datos existentes.
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `contactos` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`usuarioId` INTEGER NOT NULL, " +
                            "`nombre` TEXT NOT NULL, " +
                            "`telefono` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_contactos_usuarioId_telefono` " +
                            "ON `contactos` (`usuarioId`, `telefono`)"
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
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4)
                    .build()

                INSTANCE = nuevaBase
                nuevaBase
            }
        }
    }
}