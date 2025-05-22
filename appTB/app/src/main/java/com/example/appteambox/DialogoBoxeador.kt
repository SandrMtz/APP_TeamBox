package com.example.appteambox

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appteambox.model.Boxeador
import java.time.LocalDate

@Composable
fun DialogoBoxeador(
    boxeadorExistente: Boxeador?,
    clubId: Int,
    onConfirmar: (Boxeador) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(boxeadorExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(boxeadorExistente?.apellido ?: "") }
    var dni by remember { mutableStateOf(boxeadorExistente?.dni_boxeador ?: "") }
    var generoMasculino by remember { mutableStateOf(boxeadorExistente?.genero ?: true) }
    var peso by remember { mutableStateOf(boxeadorExistente?.peso?.toString() ?: "") }
    var comunidad by remember { mutableStateOf(boxeadorExistente?.comunidad ?: "") }
    var provincia by remember { mutableStateOf(boxeadorExistente?.provincia ?: "") }
    //var fotoPerfilBase64 by remember { mutableStateOf(boxeadorExistente?.foto_perfil ?: "") }
    var fechaNacimiento by remember { mutableStateOf(boxeadorExistente?.fecha_nacimiento ?: "") }

    val context = LocalContext.current
    //val scope = rememberCoroutineScope()

    /*val launcherSeleccionImagen = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val base64 = withContext(Dispatchers.IO) {
                    val bitmap = getBitmapFromUri(context, it)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    Base64.encodeToString(byteArray, Base64.DEFAULT)
                }
                fotoPerfilBase64 = base64
            }
        }
    }*/

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (boxeadorExistente == null) "Añadir Boxeador" else "Editar Boxeador") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") })
                OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") })
                OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso") })
                OutlinedTextField(value = comunidad, onValueChange = { comunidad = it }, label = { Text("Comunidad") })
                OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") })
                OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de Nacimiento") })

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(selected = generoMasculino, onClick = { generoMasculino = true })
                    Text("Masculino", modifier = Modifier.padding(end = 8.dp))
                    RadioButton(selected = !generoMasculino, onClick = { generoMasculino = false })
                    Text("Femenino")
                }


            }
        },
        confirmButton = {
            Button(onClick = {
                val dniRegex = Regex("^[0-9]{8}[A-Za-z]\$")        // DNI: 8 números + 1 letra
                val nieRegex = Regex("^[XYZxyz][0-9]{7}[A-Za-z]\$") // NIE: 1 letra (X/Y/Z) + 7 números + 1 letra
                val fechaRegex = Regex("^\\d{4}-\\d{2}-\\d{2}\$")  // Fecha: YYYY-MM-DD



                val dniValido = dniRegex.matches(dni) || nieRegex.matches(dni)
                val fechaValida = fechaRegex.matches(fechaNacimiento)

                if (!dniValido) {
                    Toast.makeText(context, "DNI/NIE inválido. Debe ser 8 números y una letra o letra + 7 números + letra.", Toast.LENGTH_LONG).show()
                    return@Button
                }
                if (!fechaValida) {
                    Toast.makeText(context, "Fecha de nacimiento inválida. Formato requerido: YYYY-MM-DD", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val boxeador = Boxeador(
                    Id_boxeador = boxeadorExistente?.Id_boxeador ?: 0,
                    nombre = nombre,
                    apellido = apellido,
                    dni_boxeador = dni,   // Aquí ponemos el DNI o NIE validado
                    genero = generoMasculino,
                    peso = peso.toFloatOrNull() ?: 0f,
                    comunidad = comunidad,
                    provincia = provincia,
                    //foto_perfil = if (fotoPerfilBase64.isNotEmpty()) fotoPerfilBase64 else null,
                    fecha_nacimiento = fechaNacimiento,
                    categoria = calcularCategoria(fechaNacimiento),
                    club_id = clubId,
                    nombre_club = boxeadorExistente?.nombre_club ?: "Nombre Club Desconocido",
                    fecha_registro = boxeadorExistente?.fecha_registro ?: obtenerFechaActual()
                )

                onConfirmar(boxeador)
            }) {
                Text("Guardar")
            }

        },
        dismissButton = {
            Button(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

fun calcularCategoria(fechaNacimiento: String): String {
    // Extraer el año de nacimiento (de los primeros 4 caracteres)
    val anioNacimiento = fechaNacimiento.take(4).toIntOrNull() ?: return "Desconocida"

    // Obtener el año actual
    val anioActual = LocalDate.now().year

    // Calcular la edad
    val edad = anioActual - anioNacimiento

    // Devolver la categoría según la lógica del evento SQL
    return when {
        edad > 18 -> "Élite"
        edad in 17..18 -> "Joven"
        edad in 15..16 -> "Junior"
        edad in 13..14 -> "Cadete"
        edad in 11..12 -> "Infantil"
        edad in 9..10 -> "Benjamin"
        else -> "Prebenjamin"
    }
}
fun obtenerFechaActual(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}


/*if (fotoPerfilBase64.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter("data:image/png;base64,$fotoPerfilBase64"),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Button(onClick = { launcherSeleccionImagen.launch("image/*") }) {
                    Text(if (fotoPerfilBase64.isEmpty()) "Seleccionar Foto de Perfil" else "Cambiar Foto de Perfil")
                }*/