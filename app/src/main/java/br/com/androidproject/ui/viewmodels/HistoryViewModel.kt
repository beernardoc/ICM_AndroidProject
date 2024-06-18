package br.com.androidproject.ui.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.repository.RouteRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.log


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteEntity>>(emptyList())
    val routes: MutableStateFlow<List<RouteEntity>> = _routes
    private val authUser = Firebase.auth.currentUser?.uid

    init {
        fetchRoutes()
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            Log.d("HistoryViewModel", "user id: $authUser")

            routeRepository.routes.collect {
                _routes.value = it
            }

        }
    }
}



