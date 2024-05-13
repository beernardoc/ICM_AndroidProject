package br.com.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import br.com.androidproject.authentication.FirebaseAuthRepository
import br.com.androidproject.ui.states.SignUpUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {



    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private val _signUpIsSuccessful = MutableSharedFlow<Boolean>()
    val signUpIsSuccessful = _signUpIsSuccessful


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
                },
                onConfirmPasswordChange = { password ->
                    _uiState.update {
                        it.copy(confirmPassword = password)
                    }
                }
            )
        }
    }

    suspend fun signUp() {
        try {
            val email = uiState.value.email
            val password = uiState.value.password
            val confirmPassword = uiState.value.confirmPassword

            if (password != confirmPassword) {
                _uiState.update {
                    it.copy(error = "Passwords do not match")
                }
                return
            }

            firebaseAuthRepository.signUp(email, password)
            _signUpIsSuccessful.emit(true)

        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = e.message)
            }
        }

    }

}