package br.com.androidproject.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) {



    private val _currenteUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser = _currenteUser

    init {
        firebaseAuth.addAuthStateListener {
            _currenteUser.value = it.currentUser
        }

    }

    suspend fun signUp(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .await()


    suspend fun signIn(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .await()

    fun signOut() = firebaseAuth.signOut()


}