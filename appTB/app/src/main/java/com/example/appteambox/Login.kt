package com.example.appteambox

import android.annotation.SuppressLint
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appteambox.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Login(
    navController: NavController,
    botonColors: ButtonColors,

) {
    val viewModel: LoginViewModel = viewModel()

    val email by viewModel.email.collectAsState()
    val visibilidadContrasena = viewModel.visibilidadContrasena
    val navigateTo by viewModel.navigateTo.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            navController.navigate(it)
            viewModel.resetNavigation()
        }
    }

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
                onValueChange = { viewModel.email.value = it },
                placeholder = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(0.8f),
                textStyle = TextStyle(color = Color.White)
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.contrasena,
                onValueChange = { viewModel.contrasena = it },
                placeholder = { Text("Contraseña") },
                textStyle = TextStyle(color = Color.White),
                visualTransformation = if (visibilidadContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.visibilidadContrasena = !visibilidadContrasena
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (visibilidadContrasena)
                                    R.drawable.ojoabierto else R.drawable.ojocerrado
                            ),
                            contentDescription = if (visibilidadContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login(context) },  // <-- aquí pasa el context
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = botonColors
            ) {
                Text("Iniciar sesión")
            }

            if (viewModel.isLoading) {
                Spacer(modifier = Modifier.height(10.dp))
                CircularProgressIndicator(color = Color.White)
            }

            if (viewModel.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(viewModel.errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate("RegistroCuenta") }) {
                Text("¿No tienes cuenta? Regístrate", color = Color.White)
            }
        }
    }
}
