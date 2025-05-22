package com.example.appteambox

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaUsuarioClub(navController: NavController) {
    // Estado para controlar la pestaña seleccionada (usada en la barra inferior)
    val selectedTab = remember { mutableStateOf(2) }

    // Instancia del ViewModel que maneja la lógica y datos de búsqueda de boxeadores
    val viewModel: BusquedaBoxeadorViewModel = viewModel()

    // Variables que observan el estado de los resultados, mensaje de error y carga desde el ViewModel
    val resultados by viewModel.resultados.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estados para los campos del formulario de filtros
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nombreClub by remember { mutableStateOf("") }
    var pesoMin by remember { mutableStateOf("") }
    var pesoMax by remember { mutableStateOf("") }
    var generoSeleccionado by remember { mutableStateOf<Boolean?>(null) }

    // Lista fija de comunidades autónomas para el desplegable de selección única
    val comunidadesDisponibles = listOf(
        "Andalucía", "Aragón", "Asturias", "Islas Baleares", "Canarias",
        "Cantabria", "Castilla y León", "Castilla-La Mancha", "Cataluña",
        "Comunidad Valenciana", "Extremadura", "Galicia", "Madrid",
        "Murcia", "Navarra", "La Rioja", "País Vasco", "Ceuta", "Melilla"
    )

    // Lista fija de categorías que se pueden seleccionar con checkboxes múltiples
    val categoriasDisponibles = listOf("Elite", "Joven", "Junior")

    // Estado para la comunidad seleccionada (solo una)
    var comunidadSeleccionada by remember { mutableStateOf("") }
    // Estado para las categorías seleccionadas (lista que puede tener varias)
    var categoriasSeleccionadas by remember { mutableStateOf(emptyList<String>()) }

    // Estado para controlar si el menú desplegable de comunidad está abierto o cerrado
    var expandedComunidad by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CLUB") },
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
                    0 -> {} // Aquí se podría manejar otra pestaña
                    1 -> navController.navigate("PantallaBoxeadores") // Navegar a pantalla de boxeadores
                    2 -> navController.navigate("PerfilUsuarioClub") // Navegar a perfil club
                }
            })
        }
    ) { paddingValues ->
        // Columna con scroll vertical
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF2E313B)) // Fondo oscuro
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Scroll vertical
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
        ) {
            //CAmpos para introducir los datos que queremos buscar
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
                Spacer(modifier = Modifier.width(8.dp)) // Separador horizontal entre campos
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
                // Radio button para género masculino
                RadioButton(
                    selected = generoSeleccionado == true,
                    onClick = { generoSeleccionado = true }
                )
                Text("Masculino", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                // Radio button para género femenino
                RadioButton(
                    selected = generoSeleccionado == false,
                    onClick = { generoSeleccionado = false }
                )
                Text("Femenino", color = Color.White)
            }


            Text("Comunidad Autónoma", color = Color.White)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedComunidad = true } // Al hacer click, abre dropdown
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (comunidadSeleccionada.isNotBlank()) comunidadSeleccionada else "Selecciona comunidad",
                    color = Color.Black
                )
            }
            // Menú desplegable que muestra la lista de comunidades
            DropdownMenu(
                expanded = expandedComunidad,
                onDismissRequest = { expandedComunidad = false }, // Cierra el menú al perder foco
                modifier = Modifier.fillMaxWidth()
            ) {
                comunidadesDisponibles.forEach { comunidad ->
                    DropdownMenuItem(
                        text = { Text(comunidad) },
                        onClick = {
                            comunidadSeleccionada = comunidad // Selecciona comunidad
                            expandedComunidad = false // Cierra el menú
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
                            // Alterna la selección de categoría al hacer click en la fila
                            categoriasSeleccionadas = toggleSeleccion(categoriasSeleccionadas, categoria)
                        }
                ) {
                    // Checkbox para cada categoría
                    androidx.compose.material3.Checkbox(
                        checked = categoriasSeleccionadas.contains(categoria),
                        onCheckedChange = {
                            categoriasSeleccionadas = toggleSeleccion(categoriasSeleccionadas, categoria)
                        }
                    )
                    Text(categoria, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Espacio vertical


            Button(
                onClick = {
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }

            // Indicador de carga que se muestra mientras se hace la consulta al backend
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Muestra mensaje de error si lo hay
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            // Muestra los resultados devueltos por la búsqueda
            resultados.forEach {
                Text("Club : ${it.nombre_club} ", color = Color.White)
                Text("Boxeador : ${it.nombre} ${it.apellido} - ${it.peso} kg ", color = Color.Gray)
                Text("${it.categoria}  ${if (it.genero == true) "Masculino" else "Femenino"}  ${it.provincia}", color = Color.Gray)
            }
        }
    }
}

// Función auxiliar para añadir o quitar un elemento de una lista de selección (checkboxes)
fun toggleSeleccion(lista: List<String>, item: String): List<String> {
    return if (lista.contains(item)) lista - item else lista + item
}

@Preview(showBackground = true)
@Composable
fun PreviewBusquedaUsuarioClub() {
    val dummyNavController = rememberNavController()
    PerfilUsuarioClub(navController = dummyNavController)
}
