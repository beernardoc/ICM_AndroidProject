package br.com.androidproject.ui.screens

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.ui.viewmodels.HistoryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = viewModel()
) {
    val routesState = historyViewModel.routes.collectAsState()

    Scaffold(
        topBar = {
            TopBar(modifier = Modifier.height(56.dp).background(Color(255, 255, 255)))
        }
    ) { paddingValues ->
        LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(routesState.value.reversed()) { route -> // Use routesState.value.reversed()
            RouteCard(route)
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(route: RouteEntity) {
    val startLocation = LatLng(route.startLat, route.startLng)
    val endLocation = LatLng(route.endLat, route.endLng)
    val cameraPositionState = rememberCameraPositionState()

    val context = LocalContext.current

    // State variables to hold addresses
    var startAddress by remember { mutableStateOf("") }
    var endAddress by remember { mutableStateOf("") }

    fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return if (minutes > 0) {
            "$minutes min $remainingSeconds s"
        } else {
            "$seconds s"
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, startLocation, endLocation) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val geocoder = Geocoder(context)
            val startAddresses = geocoder.getFromLocation(startLocation.latitude, startLocation.longitude, 1)
            val endAddresses = geocoder.getFromLocation(endLocation.latitude, endLocation.longitude, 1)

            if (!startAddresses.isNullOrEmpty()) {
                startAddress = startAddresses[0]?.getAddressLine(0) ?: ""

            }
            if (!endAddresses.isNullOrEmpty()) {
                endAddress = endAddresses[0]?.getAddressLine(0) ?: ""

            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = route.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0,0,0),
            )

            Spacer(modifier = Modifier.height(8.dp))

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = CameraPositionState(
                    position = CameraPosition(
                        startLocation,
                        15f,
                        10f,
                        0f
                    )

                )
            ) {

                Marker(
                    state = MarkerState(startLocation),
                    title = "Start Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
                Marker(
                    state = MarkerState(endLocation),
                    title = "End Location",

                )
                val routePoints = route.points.map { point -> LatLng(point.lat, point.lng) }
                if (routePoints.isNotEmpty()) {
                    Polyline(points = routePoints)
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Start Location",
                    tint = Color( 0,232,0)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Start: $startAddress",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "End Location",
                    tint = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "End: $endAddress",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Distance: ${"%.2f".format(route.distance)} meters",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Duration: ${formatDuration(route.duration)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Pace: ${route.pace}",
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(modifier = Modifier.height(8.dp))

        }
    }


}
