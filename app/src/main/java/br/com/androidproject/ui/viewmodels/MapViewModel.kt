package br.com.androidproject.ui.viewmodels

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.androidproject.database.AndroidProjectDB
import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.Loc
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.model.Route
import br.com.androidproject.repository.RouteRepository
import br.com.androidproject.R
import br.com.androidproject.ui.states.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import android.content.Context
import br.com.androidproject.authentication.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class MapViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseAuthRepository = FirebaseAuthRepository(firebaseAuth)

    private val _uiState = MutableStateFlow(MapState())
    val uiState = _uiState.asStateFlow()





    private var lastLocation: LatLng? = null
    private var startTime: Long = 0L
    private var timerJob: Job? = null
    private var pauseJob: Job? = null
    private val NOTIFICATION_ID = 123 // Identificador único para a notificação
    private val CHANNEL_ID = "route_notification_channel" // ID do canal de notificação

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
                        pace = updatedPace,
                        points = state.points + Loc(newLocation.latitude, newLocation.longitude)
                    )
                }

                Log.d("MapViewModel", "Updated actualLoc: ${uiState.value.actualLoc}, distance: ${uiState.value.distance}, pace: ${uiState.value.pace}")
            } ?: Log.d("MapViewModel", "Location result is null")
        }
    }


    @SuppressLint("MissingPermission")
    fun startRace() {
        fusedLocationClient?.let { getDeviceLocation(it) }


        _uiState.update {
            it.copy(
                isRunning = true,
                distance = 0f,
                initialLoc = it.actualLoc,// Reset distance when starting a new race
                totalTime = 0L, // Reset elapsed time
                pace = "", // Reset pace
                points = if (uiState.value.initialLoc != null) {
                    it.points + Loc(uiState.value.initialLoc!!.latitude, uiState.value.initialLoc!!.longitude)
                } else {
                    it.points
                }
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


    fun stopRace(title: String) {

        val currentUser = Firebase.auth.currentUser
        Log.d("points", uiState.value.points.toString())
        val newRoute =Route(
            title = title,
            startLat = uiState.value.initialLoc?.latitude ?: 0.0,
            startLng = uiState.value.initialLoc?.longitude ?: 0.0,
            endLat = uiState.value.actualLoc?.latitude ?: 0.0,
            endLng = uiState.value.actualLoc?.longitude ?: 0.0,
            distance = uiState.value.distance,
            duration = uiState.value.totalTime,
            userId = currentUser?.uid ?: "",
            points = uiState.value.points
        )

        viewModelScope.launch {
            routeRepository.save(newRoute)
            routeRepository.routes.collect {
                Log.d("MapViewModel", "Routes: $it")
            }
        }

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


    fun showRouteNotification(context: Context, distance: Float, elapsedTime: Long, pace: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Criar o canal de notificação (apenas necessário a partir do Android Oreo)
        createNotificationChannel(context)



        // Conteúdo da notificação
        val contentText = "Distance: $distance meters\nElapsed Time: $elapsedTime\nPace: $pace min/km"

        // Construir a notificação
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ícone da notificação (substitua por um ícone adequado)
            .setContentTitle("Race Information")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Exibir a notificação
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Route Notification Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = "Channel for route notifications"
            }

            // Registrar o canal de notificação no NotificationManager
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeRouteNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }



}