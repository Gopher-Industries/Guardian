package deakin.gopher.guardian.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import deakin.gopher.guardian.DataBase.DataClasses.Login

@Database(entities = arrayOf(Login::class), version = 1, exportSchema = false)
abstract class Database_ : RoomDatabase() {

    abstract fun Dao(): DaoQuery

    companion object {
        @Volatile
        var Instance: Database_? = null

        @Synchronized
        fun getDatabase(context: Context): Database_ {


            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database_::class.java,
                    "Guardiandb"
                ).allowMainThreadQueries().build()
                Instance = instance
                instance
            }

        }


    }
}