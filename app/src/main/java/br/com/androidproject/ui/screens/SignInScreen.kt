package br.com.androidproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.androidproject.ui.states.SignInUiState
import br.com.androidproject.ui.theme.AndroidProjectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    uiState: SignInUiState,
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isError = uiState.error != null
        AnimatedVisibility(visible = isError) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = uiState.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Ícone do usuário",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(124.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                text = "TrackMyRun",
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.size(24.dp))
            val textFieldModifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(vertical = 8.dp)
            OutlinedTextField(
                value = uiState.email,
                onValueChange = uiState.onEmailChange,
                modifier = textFieldModifier,
                shape = RoundedCornerShape(25),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Ícone de usuário"
                    )
                },
                label = {
                    Text(text = "Email")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                )
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = uiState.onPasswordChange,
                modifier = textFieldModifier,
                shape = RoundedCornerShape(25),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Ícone de senha"
                    )
                },
                label = {
                    Text("Password")
                },
                trailingIcon = {
                    val trailingIconModifier = Modifier.clickable {
                        uiState.onTogglePasswordVisibility()
                    }
                    if (uiState.isShowPassword) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Ícone de visível",
                            modifier = trailingIconModifier
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "Ícone de não visível",
                            modifier = trailingIconModifier
                        )
                    }
                },
                visualTransformation = if (uiState.isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(25)
            ) {
                Text(text = "Entrar")
            }
            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Registar", color = MaterialTheme.colorScheme.primary)
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
