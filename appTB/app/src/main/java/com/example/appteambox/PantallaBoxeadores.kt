package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appteambox.model.Boxeador
import com.example.appteambox.ui.club.DialogoBoxerRegistro
import com.example.appteambox.viewmodel.BoxeadorViewModel
import com.example.appteambox.viewmodel.SessionViewModel
import com.example.appteambox.viewmodel.UsuarioViewModel

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBoxeadores(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel(),
    boxeadorViewModel: BoxeadorViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {

    val boxeadores by remember { derivedStateOf { boxeadorViewModel.boxeadores } }
    val idUsuario by sessionViewModel.idUsuario.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val selectedTab = remember { mutableStateOf(1) }
    val seleccionados = remember { mutableStateListOf<Boxeador>() }
    var boxeadorEditar by remember { mutableStateOf<Boxeador?>(null) }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }

    // Estado para mostrar diálogo de registro
    var mostrarDialogoBoxerRegistro by remember { mutableStateOf(false) }

    LaunchedEffect(idUsuario) {
        idUsuario?.let { boxeadorViewModel.cargarBoxeadores(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CLUB") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorClub") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (seleccionados.isNotEmpty()) {
                        IconButton(onClick = {
                            seleccionados.forEach { boxeadorViewModel.eliminarBoxeador(it.Id_boxeador) }
                            seleccionados.clear()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.papelera),
                                contentDescription = "Eliminar seleccionados",
                                tint = Color.Black
                            )
                        }
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
                        0 -> navController.navigate("BusquedaUsuarioClub")
                        1 -> {}
                        2 -> navController.navigate("PerfilUsuarioClub")
                    }
                }
            )
        },
        content = { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF2E313B))
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (boxeadores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay boxeadores registrados", color = Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(boxeadores) { boxeador ->
                            BoxeadorItemSelectable(
                                boxeador = boxeador,
                                seleccionado = seleccionados.contains(boxeador),
                                onSeleccionar = { isSelected ->
                                    if (isSelected) seleccionados.add(boxeador)
                                    else seleccionados.remove(boxeador)
                                },
                                onEditar = {
                                    boxeadorEditar = it
                                    mostrarDialogoEditar = true
                                }
                            )
                        }
                    }
                }

                // Botón para abrir el diálogo de añadir boxeador
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { mostrarDialogoBoxerRegistro = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "Añadir Boxeador", fontSize = 16.sp)
                    }
                }
            }
        }
    )

    // Diálogo para editar boxeador existente
    if (mostrarDialogoEditar && boxeadorEditar != null && idUsuario != null) {
        DialogoBoxeador(
            boxeadorExistente = boxeadorEditar,
            clubId = idUsuario,
            onCancelar = {
                mostrarDialogoEditar = false
                boxeadorEditar = null
            },
            onConfirmar = { actualizado ->
                boxeadorViewModel.editarBoxeador(actualizado)
                mostrarDialogoEditar = false
                boxeadorEditar = null
            }
        )
    }

    // Diálogo para añadir nuevo boxeador
    if (mostrarDialogoBoxerRegistro && idUsuario != null) {
        DialogoBoxerRegistro(
            onCancelar = { mostrarDialogoBoxerRegistro = false },
            onConfirmar = { nuevoBoxeador ->
                boxeadorViewModel.agregarBoxeador(nuevoBoxeador)
                mostrarDialogoBoxerRegistro = false
            },
            boxeadorViewModel = boxeadorViewModel,
            clubId = idUsuario,
            nombreClub = "" // Cambia si tienes el nombre del club
        )
    }
}


@Composable
fun BoxeadorItemSelectable(
    boxeador: Boxeador,
    seleccionado: Boolean,
    onSeleccionar: (Boolean) -> Unit,
    onEditar: (Boxeador) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = seleccionado,
                onCheckedChange = onSeleccionar
            )

            /*Image(
                painter = rememberAsyncImagePainter(base64ToBitmap(boxeador.foto_perfil)),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )*/

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${boxeador.nombre} ${boxeador.apellido} - ${boxeador.peso} kg",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${boxeador.categoria} • ${if (boxeador.genero) "Masculino" else "Femenino"} • ${boxeador.provincia}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { onEditar(boxeador) }) {
                Icon(
                    painter = painterResource(id = R.drawable.lapiz),
                    contentDescription = "Editar",
                    tint = Color.DarkGray
                )
            }
        }
    }
}
