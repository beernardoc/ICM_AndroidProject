package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.log


@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState = _uiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        Log.d("MapViewModel", "getDeviceLocation: ")
        try {
            val locationResult = fusedLocationProviderClient.getCurrentLocation(
                100,
                null)
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val newLocation = LatLng(task.result.latitude, task.result.longitude)
                    _uiState.update {
                        it.copy(initialLocation = newLocation)
                    }
                    Log.d("MapViewModel", "Updated state: ${uiState.value.initialLocation}")
                } else {
                    Log.d("MapViewModel", "Task was not successful or result is null")
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }
    }
}