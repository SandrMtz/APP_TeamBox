package com.example.appteambox

import androidx.compose.material3.Icon
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuInferiorCLub(navController: NavController) {
    val selectedTab = remember { mutableStateOf(0) } // Controlar la pestaña seleccionada

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedTabIndex = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    // Navegar según el índice de la pestaña seleccionada
                    when (index) {
                        0 -> navController.navigate("") // Navegar a la pantalla de Reservas
                        1 -> navController.navigate("") // Navegar a la pantalla de Favoritos
                        2 -> navController.navigate("perfilUsuario") // Navegar a la pantalla de Perfil
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
                text = when (selectedTab.value) {
                    0 -> "¡Bienvenido!"
                    1 -> "¡Bienvenido!"
                    2 -> "¡Bienvenido!"
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
fun BottomNavigationBar(
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
                    contentDescription = "Reservas"
                )
            },
            label = { Text("Reservas") },
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Favoritos"
                )
            },
            label = { Text("Favoritos") },
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