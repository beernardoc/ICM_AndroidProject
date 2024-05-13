package br.com.androidproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.androidproject.ui.states.SignInUiState
import br.com.androidproject.ui.theme.AndroidProjectTheme
import br.com.androidproject.ui.theme.Typography

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(modifier.fillMaxSize()) {

        val isError = uiState.error != null
        AnimatedVisibility(visible = isError) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.error)
            ){
                Text(
                    text = uiState.error ?: "",
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onError
                )

            }

        }

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Done,
                contentDescription = "Ícone minhas tarefas",
                Modifier
                    .clip(CircleShape)
                    .size(124.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Minhas tarefas", style = Typography.titleLarge)
            Spacer(modifier = Modifier.size(16.dp))
            val textFieldModifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
            OutlinedTextField(
                value = uiState.email,
                onValueChange = uiState.onEmailChange,
                textFieldModifier,
                shape = RoundedCornerShape(25),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "ícone de usuário"
                    )
                },
                label = {
                    Text(text = "Usuário")
                }
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = uiState.onPasswordChange,
                textFieldModifier,
                shape = RoundedCornerShape(25),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Password,
                        contentDescription = "ícone de usuário"
                    )
                },
                label = {
                    Text("Senha")
                },
                trailingIcon = {
                    val trailingIconModifier = Modifier.clickable {
                        uiState.onTogglePasswordVisibility()
                    }
                    when (uiState.isShowPassword) {
                        true -> {
                            Icon(
                                Icons.Filled.Visibility,
                                contentDescription = "ícone de visível",
                                trailingIconModifier
                            )
                        }

                        else -> Icon(
                            Icons.Filled.VisibilityOff,
                            contentDescription = "ícone de não visível",
                            trailingIconModifier
                        )
                    }
                },
                visualTransformation = when (uiState.isShowPassword) {
                    false -> PasswordVisualTransformation()
                    true -> VisualTransformation.None
                }
            )
            Button(
                onClick = onSignInClick,
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
            ) {
                Text(text = "Entrar")
            }
            TextButton(
                onClick = onSignUpClick,
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
            ) {
                Text(text = "Cadastrar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    AndroidProjectTheme {
        SignInScreen(
            uiState = SignInUiState(),
            onSignInClick = {},
            onSignUpClick = {}
        )
    }
}