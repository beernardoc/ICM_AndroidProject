package br.com.androidproject


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.com.androidproject.ui.navigation.authGraph
import br.com.androidproject.ui.navigation.authGraphRoute
import br.com.androidproject.ui.navigation.homeGraph
import br.com.androidproject.ui.navigation.navigateToHomeGraph
import br.com.androidproject.ui.navigation.navigateToSignIn
import br.com.androidproject.ui.navigation.navigateToSignUp
import br.com.androidproject.ui.theme.AndroidProjectTheme
import br.com.androidproject.ui.viewmodels.MapViewModel
import br.com.androidproject.ui.viewmodels.WeatherViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity() {


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }

    private fun askPermissions() = when {
        ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)

        }
        else -> {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        askPermissions()


        val auth = Firebase.auth
        Log.d("MainActivity", "auth: $auth")

        setContent {
            AndroidProjectTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = authGraphRoute
                ) {
                    authGraph(
                        onNavigateToHomeGraph = {
                            navController.navigateToHomeGraph(it)
                        }, onNavigateToSignIn = {
                            navController.navigateToSignIn(it)
                        },
                        onNavigateToSignUp = {
                            navController.navigateToSignUp()
                        }
                    )
                    homeGraph(
                        fusedLocationProviderClient = fusedLocationProviderClient,
                        mapViewModel = viewModel
                    )
                }
            }
        }
    }

}