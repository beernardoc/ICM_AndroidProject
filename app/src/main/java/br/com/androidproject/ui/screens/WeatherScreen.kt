import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.com.androidproject.notifications.SunshineNotificationWorker
import br.com.androidproject.res.getWeatherIcon
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit


@Composable
fun WeatherScreen() {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    var weatherData by remember { mutableStateOf<WeatherData?>(null) }

    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates(locationClient) { loc ->
                    location = loc
                }
            } else {
                weatherData = WeatherData(
                    description = "Permission denied",
                    temperature = 0.0,
                    pressure = 0,
                    humidity = 0,
                    windSpeed = 0.0,
                    country = "",
                    cityName = ""
                )
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

    LaunchedEffect(Unit) {
        val workRequest = PeriodicWorkRequestBuilder<SunshineNotificationWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Weather", fontSize = 30.sp, fontWeight = FontWeight.Bold)

            location?.let { loc ->
                LaunchedEffect(loc) {
                    val fetchedWeatherData = fetchWeather(loc.latitude, loc.longitude)
                    weatherData = fetchedWeatherData
                }
            }

            weatherData?.let { data ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "in ${data.cityName}, ${data.country}", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(id = getWeatherIcon(data.description)),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = data.description, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBox("Temperature", "${data.temperature}°C")
                        InfoBox("Pressure", "${data.pressure} hPa")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBox("Humidity", "${data.humidity}%")
                        InfoBox("Wind Speed", "${data.windSpeed} m/s")
                    }
                }
            }?: run {
                CircularProgressIndicator(color = Color.Blue)
            }
        }
    }
}

@Composable
fun InfoBox(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp)
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
