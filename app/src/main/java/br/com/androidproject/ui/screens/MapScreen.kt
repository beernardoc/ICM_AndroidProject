package br.com.androidproject.ui.screens

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.androidproject.ui.viewmodels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.concurrent.TimeUnit


@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel()
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val openAlertDialog = remember { mutableStateOf(false) }

    val mapProperties = MapProperties(
        isMyLocationEnabled = uiState.actualLoc != null,
    )
    val cameraPositionState = rememberCameraPositionState()

    if (uiState.isRunning) {
        mapViewModel.showRouteNotification(
            context = LocalContext.current,
            distance = uiState.distance,
            elapsedTime = uiState.totalTime,
            pace = uiState.pace
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
         TopBar(modifier = Modifier.height(56.dp).background(Color(255, 255, 255)))


        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Move GoogleMap to the bottom of the Box
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                cameraPositionState = CameraPositionState(
                    position = CameraPosition(
                        uiState.actualLoc ?: LatLng(0.0, 0.0),
                        15f,
                        10f,
                        0f
                    )
                )

            ) {

            }

            if (uiState.isRunning) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Race Information",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Espaçamento entre títulos e conteúdos
                            Text(
                                text = "Distance: ${uiState.distance} meters",
                                style = MaterialTheme.typography.bodySmall, // Usa um estilo de corpo para texto secundário
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Elapsed Time: ${formatElapsedTime(uiState.totalTime)}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Pace: ${uiState.pace} min/km",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp) // Mais espaço antes do botão para separação visual
                            )
                        }
                    }
                }
            }

            // Botão de início/parada da corrida
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                val buttonColors =
                    if (uiState.isRunning) ButtonColors(
                        containerColor = Color(255, 105, 97),
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.Gray
                    )
                    else ButtonColors(
                        containerColor = Color(64, 196, 99),
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.Gray
                    )
                val buttonText = if (uiState.isRunning) "Stop Race" else "Start Race"
                val buttonIcon = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow
                val buttonAction = if (uiState.isRunning) {
                    {
                        openAlertDialog.value = true
                    }
                } else {
                    {
                        mapViewModel.startRace()
                    }
                }

                Button(
                    onClick = { buttonAction() },
                    colors = buttonColors
                ) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = buttonText,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (openAlertDialog.value) {
                mapViewModel.pauseRace()
                Dialog(onDismissRequest = { TODO() }) {
                    // Draw a rectangle shape with rounded corners inside the dialog
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Finish the race?",
                                modifier = Modifier.padding(16.dp),

                                )

                            var title by remember { mutableStateOf("") }
                            TextField(
                                value = title,
                                onValueChange = { title = it },
                                modifier = Modifier.padding(16.dp),
                                placeholder = { Text("Enter a title for your race") },

                                )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                TextButton(
                                    onClick = { openAlertDialog.value = false; mapViewModel.restartRace() },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Text("Dismiss")
                                }
                                TextButton(
                                    onClick = { mapViewModel.stopRace(title); openAlertDialog.value = false},
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }


            }
        }

    }




}



// Função de formatação para o tempo decorrido
// Função de formatação para o tempo decorrido
@Composable
fun formatElapsedTime(elapsedTime: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)

        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Track My Run",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
