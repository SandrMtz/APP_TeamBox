package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuInferiorClub(navController: NavController) {
    val selectedTab = remember { mutableStateOf(0) } // Controlar la pestaña seleccionada

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBarClub(
                selectedTabIndex = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    // Navegar según el índice de la pestaña seleccionada
                    when (index) {
                        0 -> navController.navigate("BusquedaUsuarioClub") // Navegar a la pantalla de Búsqueda
                        1 -> navController.navigate("PantallaBoxeadores") // Navegar a la pantalla de Favoritos
                        2 -> navController.navigate("PerfilUsuarioClub") // Navegar a la pantalla de Perfil
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2E313B)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logob),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBarClub(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White // Fondo blanco para la barra inferior
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.busqeda),
                    contentDescription = "Búsqueda",
                    tint = if (selectedTabIndex == 0) Color(0xFF080A0C) else Color(0xFF1A1A1A)
                )
            },
            label = { Text("Búsqueda") },
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Equipo"
                )
            },
            label = { Text("Equipo") },
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil") },
            selected = selectedTabIndex == 2,
            onClick = { onTabSelected(2) }
        )
    }
}