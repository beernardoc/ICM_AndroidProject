package br.com.androidproject.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import br.com.androidproject.authentication.FirebaseAuthRepository
import androidx.navigation.NavController
@Composable
fun SettingsScreen(firebaseAuthRepository: FirebaseAuthRepository, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()
            .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(text = "Settings", fontSize = 30.sp)
            Button(onClick = {
                firebaseAuthRepository.signOut()
                navController.navigate(Screens.SignIn.screen) {
                    popUpTo(0)
                }
            }) {
                Text(text = "Sign Out")
            }
        }
    }
}


