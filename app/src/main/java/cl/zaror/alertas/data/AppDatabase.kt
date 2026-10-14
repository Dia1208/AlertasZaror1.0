package cl.zaror.alertas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlertaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alertaDao(): AlertaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val nuevaBase = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alertas_zaror.db"
                ).build()

                INSTANCE = nuevaBase
                nuevaBase
            }
        }
    }
}