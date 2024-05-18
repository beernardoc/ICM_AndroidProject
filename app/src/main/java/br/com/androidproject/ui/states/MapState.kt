package br.com.androidproject.ui.states

import android.location.Location
import br.com.androidproject.ui.clusters.ZoneClusterItem
import com.google.android.gms.maps.model.LatLng


data class MapState(
    val initialLocation: LatLng? = null,

)