package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.viewmodel.FavoritosViewModel
import com.example.appteambox.viewmodel.SessionViewModel
import com.example.appteambox.viewmodel.UsuarioViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritosMultiple(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel(),
    favoritosViewModel: FavoritosViewModel = viewModel()
) {
    val selectedTab = remember { mutableStateOf(1) }
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val idUsuario by sessionViewModel.idUsuario.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val favoritos by favoritosViewModel.favoritos.collectAsState()
    val seleccionados = favoritosViewModel.boxeadoresSeleccionados

    // Cargar favoritos cuando se obtiene el ID del usuario
    LaunchedEffect(idUsuario) {
        if (idUsuario != null) {
            favoritosViewModel.obtenerFavoritosPorClub(idUsuario!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PERFIL MULTIPLE") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorMultiple") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarMultiple(
                selectedTabIndex = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    when (index) {
                        0 -> navController.navigate("BusquedaUsuarioMultiple")
                        1 -> {}
                        2 -> navController.navigate("PantallaBoxeadoresMultiple")
                        3 -> navController.navigate("PerfilUsuarioMultiple")
                    }
                })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF2E313B))
        ) {
            when {
                isLoading || idUsuario == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                favoritos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay favoritos", color = Color.White)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp)
                    ) {
                        items(favoritos) { boxeador ->
                            BoxeadorFavoritoItem(
                                boxeador = boxeador,
                                estaSeleccionado = seleccionados.contains(boxeador.Id_boxeador),
                                onClick = {
                                    favoritosViewModel.toggleSeleccion(boxeador.Id_boxeador)
                                }
                            )
                        }
                    }

                    if (seleccionados.isNotEmpty()) {
                        Button(
                            onClick = {
                                favoritosViewModel.eliminarFavoritosSeleccionados(idUsuario!!)
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Eliminar ")
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewPantallaFavoritosMultiple() {
    val dummyNavController = rememberNavController()
    PantallaFavoritosMultiple(navController = dummyNavController)
}
