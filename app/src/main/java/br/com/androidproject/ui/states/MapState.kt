package br.com.androidproject.ui.states

import br.com.androidproject.database.entity.Loc
import com.google.android.gms.maps.model.LatLng


data class MapState(
    val actualLoc: LatLng? = null,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val initialLoc: LatLng? = null,
    val distance: Float = 0f,
    val totalTime: Long = 0,
    val activeTime: Long = 0,
    val pauseTime: Long = 0,
    val pace: String = "",
    val points: List<Loc> = emptyList()


    )