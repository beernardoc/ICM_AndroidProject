package br.com.androidproject.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoEntity(
    @PrimaryKey
    val id: String,
    val path: String,
    val longitude: Double,
    val latitude: Double,
    val routeId: String
)
