package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuInferiorClub(navController: NavController) {
    // Obtenemos la ruta actual para sincronizar el tab seleccionado con la pantalla visible
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Mapear ruta a índice para la pestaña seleccionada
    val selectedTabIndex = when (currentRoute) {
        "Búsqueda" -> 0
        "PantallaBoxeadores" -> 1
        "PerfilUsuarioClub" -> 2
        else -> 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBarClub(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    // Navegar según el índice de la pestaña seleccionada
                    when (index) {
                        0 -> {
                            if (currentRoute != "Búsqueda") {
                                navController.navigate("Búsqueda") {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    restoreState = true
                                }
                            }
                        }
                        1 -> {
                            if (currentRoute != "PantallaBoxeadores") {
                                navController.navigate("PantallaBoxeadores") {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    restoreState = true
                                }
                            }
                        }
                        2 -> {
                            if (currentRoute != "PerfilUsuarioClub") {
                                navController.navigate("PerfilUsuarioClub") {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF94B6EF)), // Fondo gris claro
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (selectedTabIndex) {
                    0 -> "¡Bienvenido a Búsqueda!"
                    1 -> "¡Bienvenido al Equipo!"
                    2 -> "¡Bienvenido al PerfilUsuarioClub!"
                    else -> ""
                },
                fontSize = 26.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
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
                    Icons.Filled.DateRange,
                    contentDescription = "Búsqueda"
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
