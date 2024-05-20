package br.com.androidproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card

import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

        // Caixa de informações
        if (uiState.isRunning){
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Race Information",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Distance: ${uiState.distance} meters",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Elapsed Time: ${formatElapsedTime(uiState.totalTime)}",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Pace: ${uiState.pace} min/km",
                            modifier = Modifier.padding(bottom = 4.dp)
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
                                onClick = { mapViewModel.stopRace(); openAlertDialog.value = false},
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


// Função de formatação para o tempo decorrido
@Composable
fun formatElapsedTime(elapsedTime: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
    return String.format("%02d:%02d", minutes, seconds)
}