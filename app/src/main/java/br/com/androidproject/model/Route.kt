package br.com.androidproject.model

import br.com.androidproject.database.entity.Loc


data class Route(


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