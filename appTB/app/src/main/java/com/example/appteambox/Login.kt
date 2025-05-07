package com.example.appteambox

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.LoginRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Login(navController: NavController, botonColors: ButtonColors) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var visibilidadContrasena by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val apiService = RetrofitClient.apiService

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2E313B)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logob),
                contentDescription = "Imagen de bienvenida",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(300.dp)
            )

            Text(
                text = "Bienvenido/a",
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                placeholder = { Text(text = "Contraseña") },
                visualTransformation = if (visibilidadContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { visibilidadContrasena = !visibilidadContrasena }) {
                        Icon(
                            painter = if (visibilidadContrasena)
                                painterResource(id = R.drawable.ojoabierto)
                            else
                                painterResource(id = R.drawable.ojocerrado),
                            contentDescription = if (visibilidadContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            val response = apiService.login(LoginRequest(email, contrasena))
                            isLoading = false
                            if (response.isSuccessful && response.body() != null) {
                                val usuario = response.body()!!
                                when {
                                    usuario.esClub && !usuario.esPromotor -> navController.navigate("PerfilUsuarioClub")
                                    usuario.esPromotor && !usuario.esClub -> navController.navigate("PerfilUsuarioPromotor")
                                    usuario.esClub && usuario.esPromotor -> navController.navigate("PerfilMultiple")
                                    else -> errorMessage = "No se pudo determinar el tipo de usuario."
                                }
                            } else {
                                errorMessage = "Email o contraseña incorrectos"
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = "Error al conectar con el servidor"
                            Log.e("LoginError", "Excepción en login", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = botonColors
            ) {
                Text("Iniciar sesión")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(10.dp))
                CircularProgressIndicator(color = Color.White)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate("RegistroCuenta") }) {
                Text("¿No tienes cuenta? Regístrate", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogIn() {
    val botonClaro = ButtonDefaults.buttonColors(
        containerColor = Color.LightGray,
        contentColor = Color.Black
    )

    Login(
        navController = rememberNavController(),
        botonColors = botonClaro
    )
}
