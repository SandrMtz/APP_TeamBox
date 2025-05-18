package com.example.appteambox

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
fun PerfilUsuarioPromotor(navController: NavController) {
    val selectedTab = remember { mutableStateOf(2) }
    val usuarioViewModel: UsuarioViewModel = viewModel()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val idUsuario = sharedPreferences.getInt("id_usuario", -1)

    val usuario by usuarioViewModel.usuario.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val errorMessage by usuarioViewModel.errorMessage.collectAsState()

    if (idUsuario != -1 && usuario == null) {
        LaunchedEffect(idUsuario) {
            usuarioViewModel.obtenerUsuarioPorId(idUsuario)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROMOTOR") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorPromotor") }) {
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
                        0 -> navController.navigate("BusquedaUsuarioPromotor")
                        1 -> navController.navigate("PantallaFavoritos")
                        2 -> {} // Ya estás en Perfil
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
                // Mostrar logo del club: si hay imagen en base64 la convertimos y mostramos, sino el icono por defecto
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val bitmap = base64ToBitmap(usuario?.logo_promotora)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Logo Promotora",
                            modifier = Modifier
                                .size(104.dp)
                                .background(Color.LightGray, shape = CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Logo Promotora",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mostrar datos del usuario
                Text(
                    text = "Nombre de la Promotora : ${usuario?.nombre_promotora}",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "Nombre : ${usuario?.nombre} ${usuario?.apellido}",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(text = "Email: ${usuario?.email}", color = Color.White, fontSize = 16.sp)
                Text(
                    text = "Fecha de Creación : ${usuario?.fecha_creacion}",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Botón para cerrar sesión
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Cerrar Sesión", fontSize = 16.sp)
                }

                // Botón para cerrar la aplicación
                Button(
                    onClick = { (navController.context as? ComponentActivity)?.finishAffinity() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 8.dp)
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
fun PreviewPerfilUsuarioPromotor() {
    val dummyNavController = rememberNavController()
    PerfilUsuarioPromotor(navController = dummyNavController)
}
