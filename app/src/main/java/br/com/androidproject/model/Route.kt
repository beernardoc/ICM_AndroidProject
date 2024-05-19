package br.com.androidproject.model


data class Route(

    val id: String,
    val title: String,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val distance: Float,
    val duration: Long,
    val userId: String

)