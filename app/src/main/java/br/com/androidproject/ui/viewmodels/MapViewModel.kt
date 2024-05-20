package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.androidproject.ui.states.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.google.maps.android.SphericalUtil;
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val _uiState = MutableStateFlow(MapState())
    val uiState = _uiState.asStateFlow()

    private var lastLocation: LatLng? = null
    private var startTime: Long = 0L
    private var timerJob: Job? = null
    private var pauseJob: Job? = null

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000 // Update location every 5 seconds (adjust as needed)
        fastestInterval = 5000 // Allow faster updates if available (optional)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val newLocation = LatLng(location.latitude, location.longitude)
                val distance = calculateDistance(lastLocation, newLocation)
                lastLocation = newLocation

                _uiState.update { state ->
                    val updatedDistance = state.distance + distance
                    val updatedPace =
                        if (uiState.value.pauseTime > 0) {
                            calculatePace(uiState.value.totalTime - uiState.value.pauseTime, uiState.value.distance)
                        } else {
                            calculatePace(uiState.value.totalTime, updatedDistance)
                        }
                    state.copy(
                        actualLoc = newLocation,
                        distance = updatedDistance,
                        pace = updatedPace
                    )
                }

                Log.d("MapViewModel", "Updated actualLoc: ${uiState.value.actualLoc}, distance: ${uiState.value.distance}, pace: ${uiState.value.pace}")
            } ?: Log.d("MapViewModel", "Location result is null")
        }
    }


    @SuppressLint("MissingPermission")
    fun startRace() {
        _uiState.update {
            it.copy(
                isRunning = true,
                initialLoc = lastLocation,
                distance = 0f, // Reset distance when starting a new race
                totalTime = 0L, // Reset elapsed time
                pace = "" // Reset pace
            )
        }

        startTime = System.currentTimeMillis()

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (uiState.value.isRunning) {
                delay(1000L) // Update every second
                val elapsedTime = System.currentTimeMillis() - startTime

                _uiState.update {
                    it.copy(totalTime = elapsedTime)
                }
            }
        }

        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )




        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }
    }

    fun pauseRace() {
        val pauseTime = System.currentTimeMillis()

        fusedLocationClient?.removeLocationUpdates(locationCallback)

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (_uiState.value.isPaused) {
                delay(1000L) // Update every second
                val elapsedPauseTime = System.currentTimeMillis() - pauseTime
                _uiState.update {
                    it.copy(pauseTime = elapsedPauseTime)
                }
            }
        }


        Log.d("MapViewModel", "pauseRace")

    }

    fun restartRace() {
        lastLocation = null
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )

        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }

        pauseJob?.cancel()
        pauseJob = null

        _uiState.update {
            it.copy(isPaused = false)
        }

        Log.d("MapViewModel", "restartRace")
    }


    fun stopRace() {
        _uiState.update {
            it.copy(isRunning = false)
        }
        timerJob?.cancel()
        timerJob = null

        Log.d("MapViewModel", "stop actualLoc: ${uiState.value.actualLoc}")
        Log.d("MapViewModel", "stop initialLoc: ${uiState.value.initialLoc}")
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        this.fusedLocationClient = fusedLocationProviderClient
        Log.d("MapViewModel", "getDeviceLocation: ")
        try {
            val locationResult = fusedLocationProviderClient.getCurrentLocation(
                100,
                null
            )

            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val newLocation = LatLng(task.result.latitude, task.result.longitude)
                    lastLocation = newLocation

                    _uiState.update {
                        it.copy(actualLoc = newLocation)
                    }

                    Log.d("MapViewModel", "Updated state: ${uiState.value.actualLoc}")
                } else {
                    Log.d("MapViewModel", "Task was not successful or result is null")
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapViewModel", "SecurityException: ${e.message}")
        }
    }


    private fun calculateDistance(start: LatLng?, end: LatLng?): Float {
        if (start == null || end == null) return 0f
        return SphericalUtil.computeDistanceBetween(start, end).toFloat()
    }

    private fun calculatePace(elapsedTime: Long, distance: Float): String {
        if (distance == 0f) return "0:00" // Evitar divisão por zero

        // Converte o tempo decorrido de milissegundos para minutos
        val elapsedMinutes = elapsedTime / 60000.0
        // Converte a distância de metros para quilômetros
        val distanceKm = distance / 1000.0

        val pace = elapsedMinutes / distanceKm

        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).toInt()

        return String.format("%d:%02d", minutes, seconds)
    }
}