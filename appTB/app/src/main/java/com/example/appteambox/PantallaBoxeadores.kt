package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appteambox.model.Boxeador
import com.example.appteambox.viewmodel.BoxeadorViewModel
import com.example.appteambox.viewmodel.SessionViewModel
import com.example.appteambox.viewmodel.UsuarioViewModel


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBoxeadores(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel(),
    boxeadorViewModel: BoxeadorViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {

    val boxeadores by remember { derivedStateOf { boxeadorViewModel.boxeadores } }
    val idUsuario by sessionViewModel.idUsuario.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val selectedTab = remember { mutableStateOf(1) } // Índice 1 si esta es la pestaña 'Equipo'

    LaunchedEffect(idUsuario) {
        idUsuario?.let { boxeadorViewModel.cargarBoxeadores(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CLUB") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorClub") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarClub(
                selectedTabIndex = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    when (index) {
                        0 -> navController.navigate("BusquedaUsuarioClub")
                        1 -> {}
                        2 -> navController.navigate("PerfilUsuarioClub")
                    }
                }
            )
        },
        content = { padding ->
            if (boxeadores.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFF2E313B)),
                    contentAlignment = Alignment.Center

                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(boxeadores) { boxeador ->
                        BoxeadorItem(boxeador)
                    }
                }
            }
        }
    )
}

@Composable
fun BoxeadorItem(boxeador: Boxeador) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(base64ToBitmap(boxeador.foto_perfil)),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Boxeador : ${boxeador.nombre} ${boxeador.apellido} - ${boxeador.peso} kg",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${boxeador.categoria}  ${if (boxeador.genero) "Masculino" else "Femenino"}  ${boxeador.provincia}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
