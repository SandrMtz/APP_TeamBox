package com.example.appteambox

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.viewmodel.UsuarioViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaUsuarioPromotor(navController: NavController) {
    val selectedTab = remember { mutableStateOf(2) }
    val usuarioViewModel: UsuarioViewModel = viewModel()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("email_usuario", "") ?: ""

    // Obtener datos del usuario
    val usuario by usuarioViewModel.usuario.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val errorMessage by usuarioViewModel.errorMessage.collectAsState()

    if (email.isNotEmpty() && usuario == null) {
        LaunchedEffect(email) {
            usuarioViewModel.obtenerUsuarioPorEmail(email)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROMOTOR") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorClub") }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarClub(selectedTabIndex = selectedTab.value, onTabSelected = { index ->
                selectedTab.value = index
                when (index) {
                    0 -> {}
                    1 -> navController.navigate("PantallaFavoritos")
                    2 -> navController.navigate("PerfilUsuarioPromotor")
                }
            })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2E313B)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else if (usuario != null) {
                // Mostrar logo del club
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {

                }



                // Botón para cerrar sesión
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE68C3A)),
                    modifier = Modifier.fillMaxWidth(0.6f).padding(vertical = 8.dp)
                ) {
                    Text(text = "Cerrar Sesión", fontSize = 16.sp)
                }

                // Botón para cerrar la aplicación
                Button(
                    onClick = { (navController.context as? ComponentActivity)?.finishAffinity() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth(0.6f).padding(vertical = 8.dp)
                ) {
                    Text(text = "Cerrar Aplicación", fontSize = 16.sp)
                }
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBusquedaUsuarioPromotor() {
    val dummyNavController = rememberNavController()
    PerfilUsuarioClub(navController = dummyNavController)
}
