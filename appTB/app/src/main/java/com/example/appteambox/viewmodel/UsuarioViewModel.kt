package com.example.appteambox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Usuario
import com.example.appteambox.model.UsuarioResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class UsuarioViewModel : ViewModel() {

    // Estado mutable que almacena el usuario actual (puede ser null si no hay usuario cargado)
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> get() = _usuario

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    // Funcion para obtener datos de un usuario con su ID
    //Realiza la llamada a la API
    fun obtenerUsuarioPorId(idUsuario: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("🔍 Buscando usuario con idUsuario=$idUsuario")
                // Realiza la llamada a la API para obtener el usuario por su ID
                val response: Response<UsuarioResponse> = RetrofitClient.apiService.obtenerUsuarioPorId(idUsuario)


                if (response.isSuccessful) {
                    val usuarioResponse = response.body()
                    println("✅ Respuesta exitosa: $usuarioResponse")

                    if (usuarioResponse != null) {
                        _usuario.value = Usuario(
                            id_usuario = usuarioResponse.id,
                            nombre = usuarioResponse.nombre,
                            apellido = usuarioResponse.apellido,
                            email = usuarioResponse.email,
                            es_club = usuarioResponse.es_club,
                            es_promotor = usuarioResponse.es_promotor,
                            es_boxeador = usuarioResponse.es_boxeador,
                            nombre_club = usuarioResponse.nombre_club,
                            logo_club = usuarioResponse.logo_club,
                            nombre_promotora = usuarioResponse.nombre_promotora,
                            logo_promotora = usuarioResponse.logo_promotora,
                            comunidad = usuarioResponse.comunidad ?: "",
                            provincia = usuarioResponse.provincia ?: "",
                            telefono1 = usuarioResponse.telefono1 ?: "",
                            telefono2 = usuarioResponse.telefono2 ?: "",
                            telefono3 = usuarioResponse.telefono3 ?: "",
                            foto_perfil = usuarioResponse.foto_perfil ?: "",
                            fecha_creacion = formatFechaDesdeString(usuarioResponse.fecha_creacion)
                        )
                    } else {
                        _errorMessage.value = "Usuario no encontrado"
                    }
                } else {
                    val error = "❌ Error ${response.code()}: ${response.errorBody()?.string() ?: response.message()}"
                    _errorMessage.value = error
                    println(error)
                }
            } catch (e: Exception) {
                val error = "❗ Excepción: ${e.message}"
                _errorMessage.value = error
                println(error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para formatear la fecha desde un String a otro formato
    private fun formatFechaDesdeString(fechaStr: String?): String {
        return if (!fechaStr.isNullOrBlank()) {
            try {
                val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val date = formatoEntrada.parse(fechaStr)
                val formatoSalida = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                date?.let { formatoSalida.format(it) } ?: "Fecha no disponible"
            } catch (e: Exception) {
                "Formato de fecha inválido"
            }
        } else {
            "Fecha no disponible"
        }
    }
}
