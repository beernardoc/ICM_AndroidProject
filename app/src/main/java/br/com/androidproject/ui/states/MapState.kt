package br.com.androidproject.ui.states

import com.google.android.gms.maps.model.LatLng


data class MapState(
    val actualLoc: LatLng? = null,
    val isRunning: Boolean = false,
    val initialLoc: LatLng? = null

    )