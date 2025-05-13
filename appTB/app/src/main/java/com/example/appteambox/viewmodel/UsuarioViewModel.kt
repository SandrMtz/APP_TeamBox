package com.example.appteambox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.Usuario
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.UsuarioResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class UsuarioViewModel : ViewModel() {

    // MutableStateFlow para gestionar el usuario
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> get() = _usuario

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Mensaje de error
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    // Función para obtener el usuario por email
    fun obtenerUsuarioPorEmail(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("🔍 Email enviado a la API: $email")

                val response: Response<UsuarioResponse> =
                    RetrofitClient.apiService.obtenerUsuarioPorEmail(email)

                if (response.isSuccessful) {
                    val usuarioResponse = response.body()
                    println("✅ Respuesta exitosa: $usuarioResponse")

                    if (usuarioResponse != null) {
                        _usuario.value = Usuario(
                            id_usuario = usuarioResponse.id,
                            nombre = usuarioResponse.nombre,
                            apellido = usuarioResponse.apellido,
                            email = usuarioResponse.email,
                            es_club = usuarioResponse.esClub,
                            es_promotor = usuarioResponse.esPromotor,
                            es_boxeador = usuarioResponse.esBoxeador,
                            nombre_club = usuarioResponse.nombre_club,
                            logo_club = usuarioResponse.logoClub,
                            nombre_promotora = usuarioResponse.nombre_promotora,
                            logo_promotora = usuarioResponse.logoPromotora,
                            comunidad = "",
                            provincia = "",
                            telefono1 = "",
                            telefono2 = "",
                            telefono3 = "",
                            foto_perfil = usuarioResponse.foto_perfil ?: "",
                            fecha_creacion = formatFechaDesdeString(usuarioResponse.fecha_creacion)

                        )
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

    // Función para formatear el timestamp de fecha de creación
    fun formatFechaDesdeString(fechaStr: String?): String {
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
