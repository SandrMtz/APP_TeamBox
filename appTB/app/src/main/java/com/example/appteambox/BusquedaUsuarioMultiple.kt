package com.example.appteambox

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.model.FiltrosBusqueda
import com.example.appteambox.viewmodel.BusquedaBoxeadorViewModel
import com.example.appteambox.viewmodel.SessionViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun BusquedaUsuarioMultiple(navController: NavController, sessionViewModel: SessionViewModel = viewModel()) {
    val selectedTab = remember { mutableStateOf(2) }
    val viewModel: BusquedaBoxeadorViewModel = viewModel()
    val resultados by viewModel.resultados.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritosSeleccionados by viewModel.favoritosSeleccionados.collectAsState()


    val idUsuario by sessionViewModel.idUsuario.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nombreClub by remember { mutableStateOf("") }
    var pesoMin by remember { mutableStateOf("") }
    var pesoMax by remember { mutableStateOf("") }
    var generoSeleccionado by remember { mutableStateOf<Boolean?>(null) }

    // Comunidades para desplegable selección única
    val comunidadesDisponibles = listOf(
        "Andalucía", "Aragón", "Asturias", "Islas Baleares", "Canarias",
        "Cantabria", "Castilla y León", "Castilla-La Mancha", "Cataluña",
        "Comunidad Valenciana", "Extremadura", "Galicia", "Madrid",
        "Murcia", "Navarra", "La Rioja", "País Vasco", "Ceuta", "Melilla"
    )

    // Categorías con checkboxes múltiples
    val categoriasDisponibles = listOf("Elite", "Joven", "Junior")

    // Estado comunidad seleccionada (única)
    var comunidadSeleccionada by remember { mutableStateOf("") }
    // Estado categorías seleccionadas (lista)
    var categoriasSeleccionadas by remember { mutableStateOf(emptyList<String>()) }

    // Estado para abrir/cerrar dropdown comunidad
    var expandedComunidad by remember { mutableStateOf(false) }

    // Para mostrar mensajes al enviar favoritos
    var mensajeEnvio by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PERFIL MÚLTIPLE") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorMultiple") }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarMultiple (
                selectedTabIndex = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    when (index) {
                        0 -> {}
                        1 -> navController.navigate("PantallaFavoritosMultiple")
                        2 -> navController.navigate("PantallaBoxeadoresMultiple")
                        3 -> navController.navigate("PerfilUsuarioMultiple")
                    }
                })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF2E313B))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Scroll vertical
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { //Campos de filtros
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )
            OutlinedTextField(
                value = nombreClub,
                onValueChange = { nombreClub = it },
                label = { Text("Nombre del Club") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )

            Row {
                OutlinedTextField(
                    value = pesoMin,
                    onValueChange = { pesoMin = it },
                    label = { Text("Peso Min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = pesoMax,
                    onValueChange = { pesoMax = it },
                    label = { Text("Peso Max") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.White)
                )
            }

            Text("Género", color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = generoSeleccionado == true,
                    onClick = { generoSeleccionado = true }
                )
                Text("Masculino", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = generoSeleccionado == false,
                    onClick = { generoSeleccionado = false }
                )
                Text("Femenino", color = Color.White)
            }

            // Menú desplegable para comunidad
            Text("Comunidad Autónoma", color = Color.White)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedComunidad = true }
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (comunidadSeleccionada.isNotBlank()) comunidadSeleccionada else "Selecciona comunidad",
                    color = Color.Black
                )
            }
            DropdownMenu(
                expanded = expandedComunidad,
                onDismissRequest = { expandedComunidad = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                comunidadesDisponibles.forEach { comunidad ->
                    DropdownMenuItem(
                        text = { Text(comunidad) },
                        onClick = {
                            comunidadSeleccionada = comunidad
                            expandedComunidad = false
                        }
                    )
                }
            }

            Text("Categorías", color = Color.White)
            categoriasDisponibles.forEach { categoria ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            categoriasSeleccionadas = toggleSeleccion(categoriasSeleccionadas, categoria)
                        }
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = categoriasSeleccionadas.contains(categoria),
                        onCheckedChange = {
                            categoriasSeleccionadas = toggleSeleccion(categoriasSeleccionadas, categoria)
                        }
                    )
                    Text(categoria, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Construye objeto filtros y llama al método del ViewModel para hacer la búsqueda
                    viewModel.busquedaBoxeadores(
                        FiltrosBusqueda(
                            nombre_o_apellido = (nombre + " " + apellido).trim().ifEmpty { null },
                            peso_min = pesoMin.toDoubleOrNull(),
                            peso_max = pesoMax.toDoubleOrNull(),
                            comunidades = if (comunidadSeleccionada.isNotBlank()) listOf(comunidadSeleccionada) else emptyList(),
                            categorias = categoriasSeleccionadas,
                            genero = generoSeleccionado,
                            nombre_club = nombreClub.ifBlank { null }
                        )
                    )
                    mensajeEnvio = "" //Limpia mensaje de envio al buscar
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            // Mostrar lista de resultados con icono favorito
            resultados.forEach { boxeador ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Club : ${boxeador.nombre_club} ", color = Color.White)
                        Text( "Boxeador : ${boxeador.nombre} ${boxeador.apellido} - ${boxeador.peso} kg ", color = Color.Gray)
                        Text( "${boxeador.categoria}  ${if (boxeador.genero == true) "Masculino" else "Femenino"}  ${boxeador.provincia}", color = Color.Gray)
                    }
                    IconButton(
                        onClick = { viewModel.toggleFavorito(boxeador.Id_boxeador) }
                    ) {
                        if (favoritosSeleccionados.contains(boxeador.Id_boxeador)) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favorito",
                                tint = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "No favorito",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Botón para enviar favoritos// Botón para añadir los favoritos seleccionados a la base de datos
            Button(
                onClick = {
                    if (idUsuario != -1) {
                        viewModel.enviarFavoritos(
                            idUsuario,
                            onSuccess = {
                                mensajeEnvio = "Favoritos guardados correctamente."
                            },
                            onError = { msg ->
                                mensajeEnvio = msg
                            }
                        )
                    } else {
                        mensajeEnvio = "Error: Usuario no identificado."
                    }
                },
                enabled = favoritosSeleccionados.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text("Añadir Favoritos")
            }

            if (mensajeEnvio.isNotEmpty()) {
                Text(text = mensajeEnvio, color = Color.Yellow, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewBusquedaUsuarioMultiple() {
    val dummyNavController = rememberNavController()
    BusquedaUsuarioPromotor(navController = dummyNavController)
}
