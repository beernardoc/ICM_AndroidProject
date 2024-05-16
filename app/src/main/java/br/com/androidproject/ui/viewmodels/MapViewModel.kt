package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import br.com.androidproject.ui.clusters.ZoneClusterItem
import br.com.androidproject.ui.clusters.ZoneClusterManager
import br.com.androidproject.ui.clusters.calculateCameraViewPoints
import br.com.androidproject.ui.clusters.getCenterOfPolygon
import br.com.androidproject.ui.states.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.model.polygonOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
            clusterItems = listOf(

            )
        )
    )

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        state.value = state.value.copy(
                            lastKnownLocation = location,
                        )
                        Log.d("MapViewModel", "Location collected: ${location.latitude}, ${location.longitude}")
                    } else {
                        Log.d("MapViewModel", "Location is null")
                    }
                } else {
                    Log.d("MapViewModel", "Task unsuccessful")
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }
    }

    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }

    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        val latLngs = state.value.clusterItems.map { it.polygonOptions }
            .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
        return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
    }



    companion object {
        private val POLYGON_FILL_COLOR = Color.parseColor("#ABF44336")
    }
}