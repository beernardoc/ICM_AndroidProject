package br.com.androidproject.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.androidproject.ui.viewmodels.MapViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel()
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val openAlertDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    var file = context.createImageFile()
    val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    var capturedImageUri by remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isTaken ->
        if (isTaken) {
            Log.d("MapScreen", "Foto tirada com sucesso: $uri")
            // Atualiza o URI da imagem capturada
            capturedImageUri = uri // Usando Uri.fromFile para obter o URI correto

            Log.d("MapScreen", "capturedImageUri: $capturedImageUri")

            // Salvar a imagem na galeria após captura bem-sucedida
            val savedUri = saveImageToGallery(context, file)
            if (savedUri != null) {
                mapViewModel.addPhoto(savedUri)
                Log.d("MapScreen", "Imagem salva na galeria: $savedUri")
            } else {
                Log.e("MapScreen", "Falha ao salvar imagem na galeria")
            }
        }
    }



    val permissionLaunch = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        }
        else
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
    }




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
         TopBar(modifier = Modifier
             .height(56.dp)
             .background(Color(255, 255, 255)))


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
            Row(
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


                if(uiState.isRunning){
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLaunch.launch(Manifest.permission.CAMERA)
                            }},
                        colors = ButtonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.White,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                        )

                    }
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


@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
    val imageFileName = "JPEG_$timeStamp.jpg"
    val image = File(externalCacheDir, imageFileName)
    return image
}

fun saveImageToGallery(context: Context, imageFile: File): Uri? {
    try {
        val resolver = context.contentResolver
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        // Salvar a imagem usando MediaStore
        val imageUrl = MediaStore.Images.Media.insertImage(
            resolver,
            bitmap,
            imageFile.name,
            "Captured Image"
        )
        Log.d("MapScreen", "Imagem com path: ${imageFile.absolutePath}")

        // Excluir o arquivo temporário após salvar na galeria
        imageFile.delete()

        return if (imageUrl != null) {
            Uri.parse(imageUrl)
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("MapScreen", "Erro ao salvar imagem na galeria: ${e.message}")
        return null
    }
}
