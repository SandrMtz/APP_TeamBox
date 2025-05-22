package com.example.appteambox.ui.club

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appteambox.calcularCategoria
import com.example.appteambox.model.Boxeador
import com.example.appteambox.obtenerFechaActual
import com.example.appteambox.viewmodel.BoxeadorViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DialogoBoxerRegistro(
    onCancelar: () -> Unit,
    onConfirmar: (Boxeador) -> Unit,
    boxeadorViewModel: BoxeadorViewModel,
    clubId: Int,
    nombreClub: String,

) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf(true) } // true = masculino
    var peso by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var comunidad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    //var fotoPerfilBase64 by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //Futura mejora
    /*val fotoPerfilLauncher = rememberLauncherForActivityResult(
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

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)
            fechaNacimiento = dateFormatter.format(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            Button(onClick = {
                val dniRegex = Regex("^[0-9]{8}[A-Za-z]\$")
                val nieRegex = Regex("^[XYZxyz][0-9]{7}[A-Za-z]\$")
                val fechaRegex = Regex("^\\d{4}-\\d{2}-\\d{2}\$")

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
                    Id_boxeador = 0,
                    nombre = nombre,
                    apellido = apellido,
                    dni_boxeador = dni,
                    genero = genero,
                    peso = peso.toFloatOrNull() ?: 0f,
                    comunidad = comunidad,
                    provincia = provincia,
                    fecha_nacimiento = fechaNacimiento,
                    categoria = calcularCategoria(fechaNacimiento),
                    club_id = clubId,
                    nombre_club = nombreClub,
                    fecha_registro = obtenerFechaActual()
                )

                onConfirmar(boxeador)

            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        },
        title = { Text("Nuevo Boxeador") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("DNI") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { nuevaFecha ->
                        // Validar formato yyyy-MM-dd
                        val regex = Regex("""\d{4}-\d{2}-\d{2}""")
                        if (nuevaFecha.isEmpty() || regex.matches(nuevaFecha)) {
                            fechaNacimiento = nuevaFecha
                        }
                    },
                    label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePicker.show() }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Género:")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = genero, onClick = { genero = true })
                    Text("Masculino")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = !genero, onClick = { genero = false })
                    Text("Femenino")
                }
                OutlinedTextField(
                    value = comunidad,
                    onValueChange = { comunidad = it },
                    label = { Text("Comunidad") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = provincia,
                    onValueChange = { provincia = it },
                    label = { Text("Provincia") },
                    modifier = Modifier.fillMaxWidth()
                )



            }
        }
    )
}


/*Spacer(modifier = Modifier.height(8.dp))
Button(onClick = { fotoPerfilLauncher.launch("image/*") }) {
    Text("Seleccionar Foto")
}
if (fotoPerfilBase64.isNotBlank()) {
    val imageBytes = Base64.decode(fotoPerfilBase64, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Foto seleccionada",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}*/