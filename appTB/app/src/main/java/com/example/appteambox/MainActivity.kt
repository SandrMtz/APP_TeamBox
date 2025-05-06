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
                        startDestination = "login",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Pantalla de inicio de sesión
                        composable("login") {
                            Login(navController = navController)
                        }

                        // Pantalla de registro
                        composable("RegistroCuenta") {
                            RegistroCuenta(
                                navController = navController,
                                botonColors = botonClaro
                            )
                        }

                        // Pantalla de menú inferior del club
                        composable("MenuInferiorClub") {
                            MenuInferiorCLub(navController = navController)
                        }

                        // Pantalla para el perfil del usuario club
                        composable("PerfilUsuarioClub") {
                            PerfilUsuarioClub(navController = navController)
                        }

                        // Pantalla para el perfil del usuario promotor
                        composable("PerfilUsuarioPromotor") {
                            PerfilUsuarioPromotor(navController = navController)
                        }

                        // Pantalla para el perfil de usuarios con múltiples roles
                        //composable("PerfilMultiples") {
                        //    PerfilMultiples() 
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
