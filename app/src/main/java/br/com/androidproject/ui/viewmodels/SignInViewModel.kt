package br.com.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import br.com.androidproject.authentication.FirebaseAuthRepository
import br.com.androidproject.ui.states.SignInUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SignInViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()
    val isAuthenticated = firebaseAuthRepository.currentUser
        .map { it != null}

    init {
        _uiState.update { currentState ->
            currentState.copy(
                onEmailChange = { user ->
                    _uiState.update {
                        it.copy(email = user)
                    }
                },
                onPasswordChange = { password ->
                    _uiState.update {
                        it.copy(password = password)
                    }
                }
            )
        }
    }

    suspend fun signIn() {
        try {
            val email = uiState.value.email
            val password = uiState.value.password
            firebaseAuthRepository.signIn(email, password)

        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Erro ao realizar login")
            }
        }
    }



}