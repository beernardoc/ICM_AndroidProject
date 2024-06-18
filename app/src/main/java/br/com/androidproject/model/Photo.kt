package br.com.androidproject.model

data class Photo(
    val id: String,
    val path: String,
    val longitude: Double,
    val latitude: Double,
    val routeId: String
)
