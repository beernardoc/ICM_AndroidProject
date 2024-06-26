package br.com.androidproject.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.androidproject.model.Photo
import java.util.UUID

data class Loc(
    val lat: Double,
    val lng: Double
)

@Entity
data class RouteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val distance: Float,
    val duration: Long,
    val userId: String,
    val points: List<Loc>,
    val pace: String,
    val photos : List<Photo>


)