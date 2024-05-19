package br.com.androidproject.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.RouteEntity

@Database(entities = [RouteEntity::class], version = 1)
abstract class AndroidProjectDB : RoomDatabase() {

    abstract fun routeDao(): RouteDao

}