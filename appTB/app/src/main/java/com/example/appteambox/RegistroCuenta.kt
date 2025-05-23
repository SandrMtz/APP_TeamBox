package com.example.appteambox

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.RegistroUsuario
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCuenta(navController: NavController, botonColors: ButtonColors) {
    var esClub by remember { mutableStateOf(false) }
    var esPromotor by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var nombreClub by remember { mutableStateOf("") }
    var nombrePromotora by remember { mutableStateOf("") }

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var comunidad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    val telefonos = remember { mutableStateListOf("") }

    // Logos
    var logoClubUri by remember { mutableStateOf<Uri?>(null) }
    var logoPromotoraUri by remember { mutableStateOf<Uri?>(null) }
    var logoClubBase64 by remember { mutableStateOf("") }
    var logoPromotoraBase64 by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Launchers para seleccionar imagenes
    val clubLogoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            logoClubUri = it
            scope.launch {
                logoClubBase64 = uriToBase64(context, it)
            }
        }
    }

    val promotoraLogoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            logoPromotoraUri = it
            scope.launch {
                logoPromotoraBase64 = uriToBase64(context, it)
            }
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
                    val painter = rememberAsyncImagePainter(it)
                    Image(painter = painter, contentDescription = null, modifier = Modifier.size(100.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nombreClub,
                    onValueChange = { nombreClub = it },
                    label = { Text("Nombre del Club") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
            }

            // Logo para Promotora
            if (esPromotor) {
                Text("Logo de la Promotora", color = Color.White)
                Button(onClick = { promotoraLogoLauncher.launch("image/*") }, colors = botonColors) {
                    Text("Seleccionar Logo Promotora")
                }
                logoPromotoraUri?.let {
                    val painter = rememberAsyncImagePainter(it)
                    Image(painter = painter, contentDescription = null, modifier = Modifier.size(100.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nombrePromotora,
                    onValueChange = { nombrePromotora = it },
                    label = { Text("Nombre de la Promotora") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
            }

            // Campos comunes
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
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
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
            }

            if (telefonos.size < 3) {
                Button(onClick = { telefonos.add("") }, colors = botonColors) {
                    Text("Añadir Teléfono")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // Validación simple de email
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if (!email.matches(emailPattern.toRegex())) {
                    Toast.makeText(context, "Por favor, introduce un email válido", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val usuario = RegistroUsuario(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    contrasena = contrasena,
                    comunidad = comunidad,
                    provincia = provincia,
                    telefonos = telefonos,
                    es_club = esClub,
                    es_promotor = esPromotor,
                    nombre_club = if (esClub) nombreClub else null,
                    logo_club = if (esClub) logoClubBase64 else null,
                    nombre_promotora = if (esPromotor) nombrePromotora else null,
                    logo_promotora = if (esPromotor) logoPromotoraBase64 else null,
                )
                Log.d("RegistroCuenta", "Usuario a enviar: $usuario")
                registrarUsuario(usuario, context, navController)
            }, colors = botonColors, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { navController.popBackStack() }, colors = botonColors, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
fun getBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

fun registrarUsuario(
    usuario: RegistroUsuario,
    context: android.content.Context,
    navController: NavController
) {
    val call = RetrofitClient.apiService.registrarUsuario(usuario)
    call.enqueue(object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                navController.navigate("login") {
                    popUpTo("registro") { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error en el registro: ${response.code()}", Toast.LENGTH_LONG).show()
                Log.e("Registro", "Error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            Log.e("Registro", "Fallo de red", t)
        }
    })
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
