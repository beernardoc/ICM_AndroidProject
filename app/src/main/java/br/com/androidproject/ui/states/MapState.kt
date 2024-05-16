package br.com.androidproject.ui.states

import android.location.Location
import br.com.androidproject.ui.clusters.ZoneClusterItem


data class MapState(
    val lastKnownLocation: Location?,
    val clusterItems: List<ZoneClusterItem>,
)