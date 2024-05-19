import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

@Composable
fun WeatherScreen() {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    var weather by remember { mutableStateOf("Loading...") }

    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates(locationClient) { loc ->
                    location = loc
                }
            } else {
                weather = "Permission denied"
            }
        }
    )

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                startLocationUpdates(locationClient) { loc ->
                    location = loc
                }
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Weather", fontSize = 30.sp)
            location?.let { loc ->
                LaunchedEffect(loc) {
                    val weatherData = fetchWeather(loc.latitude, loc.longitude)
                    weather = weatherData ?: "Error fetching weather"
                }
            }
            Text(text = weather, fontSize = 20.sp)
        }
    }
}

fun startLocationUpdates(locationClient: FusedLocationProviderClient, onLocationUpdate: (Location) -> Unit) {
    val locationRequest = LocationRequest.create().apply {
        interval = 10000 // 10 seconds
        fastestInterval = 5000 // 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationUpdate(location)
            }
        }
    }
    try {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}
