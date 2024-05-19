package br.com.androidproject.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.androidproject.database.entity.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Query("SELECT * FROM RouteEntity")
    fun findAll(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM RouteEntity WHERE id = :id")
    fun findById(id: String): Flow<RouteEntity?>

    @Query("SELECT * FROM RouteEntity WHERE userId = :id")
    fun findByUserId(id: String): Flow<RouteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(route: RouteEntity)

    @Delete
    suspend fun delete(route: RouteEntity)

}