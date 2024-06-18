package br.com.androidproject.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class PhotoEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val path: String,
    val longitude: Double,
    val latitude: Double,
)
