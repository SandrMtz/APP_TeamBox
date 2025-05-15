package com.example.appteambox.viewmodel

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

    fun login() {
        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val response: Response<UsuarioResponse> =
                    RetrofitClient.apiService.login(LoginRequest(email.value, contrasena))

                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
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
    //Dependiendo del tipo de usuario va a un perfil u otro

    private fun handleUserType(usuario: UsuarioResponse) {
        Log.d("Login", "Usuario recibido: es_club=${usuario.es_club}, es_promotor=${usuario.es_promotor}")
        _navigateTo.value = when {
            usuario.es_club && !usuario.es_promotor -> "PerfilUsuarioClub"
            usuario.es_promotor && !usuario.es_club -> "PerfilUsuarioPromotor"
            //usuario.es_club && usuario.es_promotor -> "PerfilMultiples"
            else -> {
                errorMessage = "No se pudo determinar el tipo de usuario."
                null
            }
        }
    }
}
