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
                        composable("login") {
                            Login(
                                navController = navController,
                                botonColors = botonClaro
                            )
                        }

                        composable("RegistroCuenta") {
                            RegistroCuenta(
                                navController = navController,
                                botonColors = botonClaro
                            )
                        }

                        composable("MenuInferiorClub") {
                            MenuInferiorClub(navController = navController)
                        }

                        composable("PerfilUsuarioClub") {
                            PerfilUsuarioClub(navController = navController)
                        }
                        composable("BusquedaUsuarioClub") {
                            BusquedaUsuarioClub(navController = navController)
                        }
                        composable("PantallaBoxeadores") {
                            PantallaBoxeadores(navController = navController)
                        }


                        composable("MenuInferiorPromotor") {
                            MenuInferiorPromotor(navController = navController)
                        }

                        composable("PerfilUsuarioPromotor") {
                            PerfilUsuarioPromotor(navController = navController)
                        }
                        composable("BusquedaUsuarioPromotor") {
                            BusquedaUsuarioPromotor(navController = navController)
                        }
                        composable("PantallaFavoritos") {
                            PantallaFavoritos(navController = navController)
                        }
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
