package com.example.appteambox.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.LoginRequest
import com.example.appteambox.model.UsuarioResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {

    val email = MutableStateFlow("")
    var contrasena by mutableStateOf("")
    var visibilidadContrasena by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    // Estado de navegación como StateFlow
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    fun resetNavigation() {
        _navigateTo.value = null
    }

    // Ahora recibe context para SharedPreferences
    fun login(context: Context) {
        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val response: Response<UsuarioResponse> =
                    RetrofitClient.apiService.login(LoginRequest(email.value, contrasena))

                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!

                    // Guardar id_usuario y email en SharedPreferences
                    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putInt("id_usuario", usuario.id)
                        .putString("email_usuario", email.value)
                        .apply()

                    handleUserType(usuario)
                } else {
                    errorMessage = "Email o contraseña incorrectos"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error al conectar con el servidor"
            }
        }
    }

    private fun handleUserType(usuario: UsuarioResponse) {
        Log.d("Login", "Usuario recibido: es_club=${usuario.es_club}, es_promotor=${usuario.es_promotor}")
        _navigateTo.value = when {
            usuario.es_club && !usuario.es_promotor -> "MenuInferiorClub"
            usuario.es_promotor && !usuario.es_club -> "MenuInferiorPromotor"
            else -> {
                errorMessage = "No se pudo determinar el tipo de usuario."
                null
            }
        }
    }
}
