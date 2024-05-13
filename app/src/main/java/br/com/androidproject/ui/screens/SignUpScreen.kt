package br.com.androidproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.androidproject.ui.states.SignUpUiState
import br.com.androidproject.ui.theme.AndroidProjectTheme
import br.com.androidproject.ui.theme.Typography

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {

        AnimatedVisibility(visible = uiState.error != null) {
            uiState.error?.let {

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = it,
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        style = Typography.bodyMedium,
                        color = Color.Black
                    )
                }


            }
        }



        Text(
            text = "Cadastrando usuário",
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            style = Typography.titleLarge
        )
        Column(
            Modifier
                .fillMaxWidth(0.8f)
                .weight(1f)
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = uiState.onEmailChange,
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25),
                label = {
                    Text(text = "Email")
                }
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = uiState.onPasswordChange,
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25),
                label = {
                    Text(text = "Senha")
                }
            )
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = uiState.onConfirmPasswordChange,
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25),
                label = {
                    Text(text = "Confirmar senha")
                }
            )
            Button(
                onClick = onSignUpClick,
                Modifier.fillMaxWidth()
            ) {
                Text(text = "Cadastrar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    AndroidProjectTheme {
        SignUpScreen(
            uiState = SignUpUiState(),
            onSignUpClick = {}
        )
    }
}