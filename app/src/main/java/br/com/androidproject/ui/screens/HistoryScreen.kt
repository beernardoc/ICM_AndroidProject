package br.com.androidproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.androidproject.ui.viewmodels.HistoryViewModel
import br.com.androidproject.ui.viewmodels.MapViewModel

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = viewModel()
) {
    // Obtemos o estado atual do fluxo de rotas
    val routesState = historyViewModel.routes.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Routes",
            fontSize = 24.sp,
        )

        // Exibição simples dos itens da lista de rotas
        routesState.value.forEach { route ->
            Text(
                text = route.toString(), // Aqui você pode exibir os dados de cada rota conforme necessário
                fontSize = 16.sp
            )
        }
    }
}