package com.example.appteambox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.ui.theme.AppTeamBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTeamBoxTheme {
                val navController = rememberNavController()

                // Declaramos los colores del botón dentro de setContent
                val botonClaro = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )

                Scaffold(modifier = Modifier) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "login", // Pantalla inicial
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Pantalla de Login
                        composable("login") {
                            Login(
                                navController = navController,
                                botonColors = botonClaro
                            )
                        }

                        // Pantalla de Registro
                        composable("RegistroCuenta") {
                            RegistroCuenta(
                                navController = navController,
                                botonColors = botonClaro
                            )
                        }

                        // Menú Inferior para Club
                        composable("MenuInferiorClub") {
                            MenuInferiorCLub(navController = navController)
                        }

                        // Perfil de usuario Club
                        composable("PerfilUsuarioClub") {
                            PerfilUsuarioClub(navController = navController)
                        }

                        // Menú Inferior para Promotor
                        composable("MenuInferiorPromotor") {
                            MenuInferiorPromotor(navController = navController)
                        }

                        // Perfil de usuario Promotor
                        composable("PerfilUsuarioPromotor") {
                            PerfilUsuarioPromotor(navController = navController)
                        }

                        // Perfil Mixto (cuando esté listo)
                        // composable("PerfilMultiples") {
                        //     PerfilMultiples(navController = navController)
                        // }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTeamBoxTheme {
        Greeting("Android")
    }
}
