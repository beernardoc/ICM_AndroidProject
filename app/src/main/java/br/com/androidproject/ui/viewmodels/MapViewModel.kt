package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import br.com.androidproject.ui.states.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


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
                        it.copy(actualLoc = newLocation)
                    }
                    Log.d("MapViewModel", "Updated state: ${uiState.value}")
                } else {
                    Log.d("MapViewModel", "Task was not successful or result is null")
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }
    }

    fun startRace() {
        var actualLoc = uiState.value.actualLoc
        _uiState.update {
            it.copy(isRunning = true, initialLoc = actualLoc)
        }



    }

    fun stopRace() {
        _uiState.update {
            it.copy(isRunning = false)
        }
    }

}