package br.com.androidproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.com.androidproject.ui.navigation.authGraph
import br.com.androidproject.ui.navigation.authGraphRoute
import br.com.androidproject.ui.navigation.homeGraph
import br.com.androidproject.ui.navigation.navigateToHomeGraph
import br.com.androidproject.ui.navigation.navigateToSignIn
import br.com.androidproject.ui.navigation.navigateToSignUp
import br.com.androidproject.ui.theme.AndroidProjectTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        Log.i(TAG, "onCreate usuario atual: ${auth.currentUser}")

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
                    homeGraph()

                }
            }
        }
    }

}
