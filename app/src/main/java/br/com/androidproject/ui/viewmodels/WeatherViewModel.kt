package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import br.com.androidproject.ui.states.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor() : ViewModel({
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
                        Log.d("WeatherViewModel", "Location collected: ${location.latitude}, ${location.longitude}")
                    } else {
                        Log.d("WeatherViewModel", "Location is null")
                    }
                } else {
                    Log.d("WeatherViewModel", "Task unsuccessful")
                }
            }
        } catch (e: SecurityException) {
            Log.e("WeatherViewModel", "SecurityException: ${e.message}")
        }
    }

})