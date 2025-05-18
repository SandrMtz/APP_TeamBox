package com.example.appteambox.ui.screens

import DialogoBoxeador
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appteambox.BottomNavigationBarClub
import com.example.appteambox.R
import com.example.appteambox.model.Boxeador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBoxeadores(
    navController: NavController,
    listaBoxeadores: List<Boxeador>,
    onAgregar: (Boxeador) -> Unit,
    onEditar: (Boxeador) -> Unit,
    onEliminar: (Boxeador) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var boxeadorSeleccionado by remember { mutableStateOf<Boxeador?>(null) }
    var selectedTab by remember { mutableStateOf(1) } // Por ejemplo, para el BottomNavigationBarClub

    Scaffold(
        containerColor = Color(0xFF2E313B),
        topBar = {
            TopAppBar(title = { Text("CLUB") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorClub") }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarClub(selectedTabIndex = selectedTab, onTabSelected = { index ->
                selectedTab = index
                when (index) {
                    0 -> navController.navigate("BusquedaUsuarioClub")
                    1 -> {  }
                    2 -> navController.navigate("PerfilUsuarioClub")
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)

        ) {
            Text("Equipo de competición", style = MaterialTheme.typography.titleLarge,
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { boxeadorSeleccionado = null // nuevo boxeador
                mostrarDialogo = true
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray,contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text ="Agregar Boxeador", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(listaBoxeadores) { boxeador ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "${boxeador.nombre} ${boxeador.apellido}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Peso: ${boxeador.peso} kg - Categoría: ${boxeador.categoria}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    boxeadorSeleccionado = boxeador
                                    mostrarDialogo = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = { onEliminar(boxeador) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoBoxeador(
            boxeadorExistente = boxeadorSeleccionado,
            onConfirmar = { boxeador ->
                if (boxeadorSeleccionado == null) {
                    onAgregar(boxeador)
                } else {
                    onEditar(boxeador)
                }
                mostrarDialogo = false
            },
            onCancelar = {
                mostrarDialogo = false
            }
        )
    }
}
