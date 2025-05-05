package com.example.appteambox

import android.graphics.Bitmap
import android.util.Base64
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCuenta(navController: NavController, botonColors: ButtonColors) {
    var esClub by remember { mutableStateOf(false) }
    var esPromotor by remember { mutableStateOf(false) }

    var nombreClub by remember { mutableStateOf("") }
    var nombrePromotora by remember { mutableStateOf("") }

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var comunidad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var telefonos by remember { mutableStateOf(mutableListOf("")) }

    // LOGOS
    var logoClubUri by remember { mutableStateOf<Uri?>(null) }
    var logoPromotoraUri by remember { mutableStateOf<Uri?>(null) }
    var logoClubBase64 by remember { mutableStateOf("") }
    var logoPromotoraBase64 by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Launchers
    val clubLogoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            logoClubUri = it
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            logoClubBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    val promotoraLogoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            logoPromotoraUri = it
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            logoPromotoraBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E313B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear cuenta", fontSize = 24.sp, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            // Selección de roles
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esClub, onCheckedChange = { esClub = it })
                    Text("Club", color = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esPromotor, onCheckedChange = { esPromotor = it })
                    Text("Promotor", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logo para Club
            if (esClub) {
                Text("Logo del Club", color = Color.White)
                Button(onClick = { clubLogoLauncher.launch("image/*") }, colors = botonColors) {
                    Text("Seleccionar Logo Club")
                }
                logoClubUri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.size(100.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nombreClub,
                    onValueChange = { nombreClub = it },
                    label = { Text("Nombre del Club") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Logo para Promotora
            if (esPromotor) {
                Text("Logo de la Promotora", color = Color.White)
                Button(onClick = { promotoraLogoLauncher.launch("image/*") }, colors = botonColors) {
                    Text("Seleccionar Logo Promotora")
                }
                logoPromotoraUri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.size(100.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nombrePromotora,
                    onValueChange = { nombrePromotora = it },
                    label = { Text("Nombre de la Promotora") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Campos comunes
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComunidadYProvinciaDropdown(
                comunidad = comunidad,
                provincia = provincia,
                onComunidadChange = {
                    comunidad = it
                    provincia = ""
                },
                onProvinciaChange = {
                    provincia = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            telefonos.forEachIndexed { index, telefono ->
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefonos[index] = it },
                    label = { Text("Teléfono ${index + 1}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (telefonos.size < 3) {
                Button(onClick = { telefonos.add("") }, colors = botonColors) {
                    Text("Añadir Teléfono")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // Aquí puedes enviar los datos al backend
                navController.navigate("login")
            }, colors = botonColors) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}




@Composable
fun ComunidadYProvinciaDropdown(
    comunidad: String,
    provincia: String,
    onComunidadChange: (String) -> Unit,
    onProvinciaChange: (String) -> Unit
) {
    val comunidades = mapOf(
        "Andalucía" to listOf("Almería", "Cádiz", "Córdoba", "Granada", "Huelva", "Jaén", "Málaga", "Sevilla"),
        "Aragón" to listOf("Huesca", "Teruel", "Zaragoza"),
        "Asturias" to listOf("Asturias"),
        "Islas Baleares" to listOf("Islas Baleares"),
        "Canarias" to listOf("Las Palmas", "Santa Cruz de Tenerife"),
        "Cantabria" to listOf("Cantabria"),
        "Castilla y León" to listOf("Ávila", "Burgos", "León", "Palencia", "Salamanca", "Segovia", "Soria", "Valladolid", "Zamora"),
        "Castilla-La Mancha" to listOf("Albacete", "Ciudad Real", "Cuenca", "Guadalajara", "Toledo"),
        "Cataluña" to listOf("Barcelona", "Girona", "Lleida", "Tarragona"),
        "Comunidad Valenciana" to listOf("Alicante", "Castellón", "Valencia"),
        "Extremadura" to listOf("Badajoz", "Cáceres"),
        "Galicia" to listOf("A Coruña", "Lugo", "Ourense", "Pontevedra"),
        "Madrid" to listOf("Madrid"),
        "Murcia" to listOf("Murcia"),
        "Navarra" to listOf("Navarra"),
        "La Rioja" to listOf("La Rioja"),
        "País Vasco" to listOf("Álava", "Guipúzcoa", "Vizcaya"),
        "Ceuta" to listOf("Ceuta"),
        "Melilla" to listOf("Melilla")
    )

    var expandedComunidad by remember { mutableStateOf(false) }
    var expandedProvincia by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        Column {
            Text("Comunidad Autónoma")
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedComunidad = true }
                .padding(8.dp)
                .background(Color.LightGray)) {
                Text(comunidad.ifBlank { "Selecciona comunidad" })
            }
            DropdownMenu(expanded = expandedComunidad, onDismissRequest = { expandedComunidad = false }) {
                comunidades.keys.forEach {
                    DropdownMenuItem(onClick = {
                        onComunidadChange(it)
                        expandedComunidad = false
                    }, text = { Text(it) })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (comunidad.isNotBlank()) {
                Text("Provincia")
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedProvincia = true }
                    .padding(8.dp)
                    .background(Color.LightGray)) {
                    Text(provincia.ifBlank { "Selecciona provincia" })
                }
                DropdownMenu(expanded = expandedProvincia, onDismissRequest = { expandedProvincia = false }) {
                    comunidades[comunidad]?.forEach {
                        DropdownMenuItem(onClick = {
                            onProvinciaChange(it)
                            expandedProvincia = false
                        }, text = { Text(it) })
                    }
                }
            }
        }
    }
}
