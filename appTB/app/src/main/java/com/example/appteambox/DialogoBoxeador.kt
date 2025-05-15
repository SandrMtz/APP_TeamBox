import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appteambox.ComunidadYProvinciaDropdown
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Boxeador
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoBoxeador(
    boxeadorExistente: Boxeador?,
    onConfirmar: (Boxeador) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados de campos con valores iniciales (para editar o nuevo)
    var nombre by remember { mutableStateOf(boxeadorExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(boxeadorExistente?.apellido ?: "") }
    var fechaNacimiento by remember { mutableStateOf(boxeadorExistente?.fecha_nacimiento ?: "") }
    var dni by remember { mutableStateOf(boxeadorExistente?.dni_boxeador ?: "") }
    var generoMasculino by remember { mutableStateOf(boxeadorExistente?.genero ?: true) }
    var peso by remember { mutableStateOf(boxeadorExistente?.peso?.toString() ?: "") }
    var comunidad by remember { mutableStateOf(boxeadorExistente?.comunidad ?: "") }
    var provincia by remember { mutableStateOf(boxeadorExistente?.provincia ?: "") }
    var fotoBase64 by remember { mutableStateOf(boxeadorExistente?.foto_perfil ?: "") }

    // Estados para validación
    var errorNombre by remember { mutableStateOf(false) }
    var errorApellido by remember { mutableStateOf(false) }
    var errorDni by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf(false) }

    // Listas para selector (ejemplo estático)
    val listaComunidades = listOf("Comunidad A", "Comunidad B", "Comunidad C")
    val provinciasPorComunidad = mapOf(
        "Comunidad A" to listOf("Provincia A1", "Provincia A2"),
        "Comunidad B" to listOf("Provincia B1", "Provincia B2"),
        "Comunidad C" to listOf("Provincia C1", "Provincia C2")
    )
    val listaProvincias = provinciasPorComunidad[comunidad] ?: emptyList()

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            if (bytes != null) {
                fotoBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            }
        }
    }

    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = if (boxeadorExistente == null) "Añadir Boxeador" else "Editar Boxeador",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    isError = errorNombre,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    isError = errorApellido,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = { Text("Fecha Nacimiento (YYYY-MM-DD)") },
                    isError = errorFecha,
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = dni,
                    onValueChange = {
                        if (it.length <= 9) dni = it.uppercase()
                    },
                    label = { Text("DNI (8 números + letra)") },
                    isError = errorDni,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Género")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = generoMasculino,
                        onClick = { generoMasculino = true }
                    )
                    Text("Masculino", modifier = Modifier.clickable { generoMasculino = true })

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = !generoMasculino,
                        onClick = { generoMasculino = false }
                    )
                    Text("Femenino", modifier = Modifier.clickable { generoMasculino = false })
                }

                OutlinedTextField(
                    value = peso,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) peso = it
                    },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Aquí debería ir tu dropdown o selector personalizado
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

                Spacer(modifier = Modifier.height(8.dp))

                Text("Foto de perfil")
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray, shape = CircleShape)
                        .clickable { launcherGaleria.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoBase64.isNotEmpty()) {
                        val imageBytes = try {
                            Base64.decode(fotoBase64, Base64.DEFAULT)
                        } catch (e: Exception) {
                            null
                        }

                        if (imageBytes != null) {
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Foto perfil",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            Text("Error al cargar imagen")
                        }
                    } else {
                        Text("Seleccionar imagen", color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onCancelar) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // Validaciones
                        errorNombre = nombre.isBlank()
                        errorApellido = apellido.isBlank()
                        errorDni = !dni.matches(Regex("^\\d{8}[A-Z]\$"))
                        errorFecha = !fechaNacimiento.matches(Regex("^\\d{4}-\\d{2}-\\d{2}\$"))

                        if (!(errorNombre || errorApellido || errorDni || errorFecha)) {
                            val nuevoBoxeador = Boxeador(
                                Id_boxeador = boxeadorExistente?.Id_boxeador ?: 0,
                                nombre = nombre.trim(),
                                apellido = apellido.trim(),
                                fecha_nacimiento = fechaNacimiento,
                                dni_boxeador = dni,
                                genero = generoMasculino,
                                peso = peso.toDoubleOrNull() ?: 0.0,
                                categoria = "", // Puedes implementar lógica para categoría
                                foto_perfil = fotoBase64,
                                comunidad = comunidad,
                                provincia = provincia,
                                club_id = boxeadorExistente?.club_id ?: 0,
                                fecha_registro = boxeadorExistente?.fecha_registro ?: ""
                            )

                            scope.launch {
                                try {
                                    RetrofitClient.apiService.crearBoxeador(nuevoBoxeador)
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    onConfirmar(nuevoBoxeador)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error en la llamada: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("DialogoBoxeador", "Error retrofit suspend", e)
                                }
                            }
                        }
                    }) {
                        Text(if (boxeadorExistente == null) "Agregar" else "Guardar")
                    }
                }
            }
        }
    }
}