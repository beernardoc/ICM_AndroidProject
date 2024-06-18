package br.com.androidproject.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import br.com.androidproject.MainActivity
import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.Loc

import br.com.androidproject.database.dao.PhotoDao
import br.com.androidproject.database.entity.PhotoEntity
import br.com.androidproject.database.entity.RouteEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Database(entities = [RouteEntity::class, PhotoEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AndroidProjectDB : RoomDatabase() {

    abstract fun routeDao(): RouteDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AndroidProjectDB? = null

        fun getDatabase(context: MainActivity): AndroidProjectDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AndroidProjectDB::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromPointsList(value: List<Loc>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Loc>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPointsList(value: String): List<Loc> {
        val gson = Gson()
        val type = object : TypeToken<List<Loc>>() {}.type
        return gson.fromJson(value, type)
    }
}