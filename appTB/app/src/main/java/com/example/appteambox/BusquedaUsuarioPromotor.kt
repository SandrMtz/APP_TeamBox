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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appteambox.model.FiltrosBusqueda
import com.example.appteambox.viewmodel.BusquedaBoxeadorViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaUsuarioPromotor(navController: NavController) {
    val selectedTab = remember { mutableStateOf(2) }
    val viewModel: BusquedaBoxeadorViewModel = viewModel()
    val resultados by viewModel.resultados.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROMOTOR") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MenuInferiorPromotor") }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBarClub(selectedTabIndex = selectedTab.value, onTabSelected = { index ->
                selectedTab.value = index
                when (index) {
                    0 -> {}
                    1 -> navController.navigate("Equipo")
                    2 -> navController.navigate("PerfilUsuarioPromotor")
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
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombreClub,
                onValueChange = { nombreClub = it },
                label = { Text("Nombre del Club") },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                OutlinedTextField(
                    value = pesoMin,
                    onValueChange = { pesoMin = it },
                    label = { Text("Peso Min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = pesoMax,
                    onValueChange = { pesoMax = it },
                    label = { Text("Peso Max") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
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
                    viewModel.busquedaBoxeadores(
                        FiltrosBusqueda(
                            nombre = nombre,
                            apellido = apellido,
                            comunidad = if (comunidadSeleccionada.isNotBlank()) listOf(comunidadSeleccionada) else emptyList(),
                            categoria = categoriasSeleccionadas,
                            genero = generoSeleccionado,
                            pesoMin = pesoMin.toDoubleOrNull(),
                            pesoMax = pesoMax.toDoubleOrNull(),
                            nombre_club = nombreClub
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray,contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            resultados.forEach {
                Text("${it.nombre} ${it.apellido} - ${it.categoria}", color = Color.White)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBusquedaUsuarioPromotor() {
    val dummyNavController = rememberNavController()
    PerfilUsuarioClub(navController = dummyNavController)
}
