package br.com.androidproject.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.androidproject.database.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM PhotoEntity")
    fun findAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM PhotoEntity WHERE id = :id")
    fun findById(id: String): Flow<PhotoEntity?>

    @Query("SELECT * FROM PhotoEntity WHERE routeId = :id")
    fun findByRouteId(id: String): Flow<PhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(route: PhotoEntity)

    @Delete
    suspend fun delete(route: PhotoEntity)
}