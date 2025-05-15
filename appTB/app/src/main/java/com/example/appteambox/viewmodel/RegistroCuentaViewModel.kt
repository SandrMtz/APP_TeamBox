package com.example.appteambox.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.RegistroUsuario
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroCuentaViewModel : ViewModel() {
    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var email by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var comunidad by mutableStateOf("")
    var provincia by mutableStateOf("")
    val telefonos = mutableStateListOf<String>()

    var esClub by mutableStateOf(false)
    var esPromotor by mutableStateOf(false)
    var esBoxeador by mutableStateOf(false)

    var nombreClub by mutableStateOf("")
    var nombrePromotora by mutableStateOf("")
    var fotoPerfilBase64 by mutableStateOf("")
    var logoClubBase64 by mutableStateOf("")
    var logoPromotoraBase64 by mutableStateOf("")

    // Validar campos antes de hacer la solicitud
    private fun validarCampos(): Boolean {
        if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || contrasena.isBlank()) {
            return false
        }
        if (esClub && nombreClub.isBlank()) {
            return false
        }
        if (esPromotor && nombrePromotora.isBlank()) {
            return false
        }
        if (esBoxeador && fotoPerfilBase64.isBlank()) {
            return false
        }
        return true
    }

    fun registrarUsuario(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (!validarCampos()) {
            onFailure("Por favor, complete todos los campos obligatorios")
            return
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
            es_boxeador = esBoxeador, // Se agrega el campo esBoxeador
            nombre_club = if (esClub) nombreClub else null,
            logo_club = if (esClub) logoClubBase64 else null,
            nombre_promotora = if (esPromotor) nombrePromotora else null,
            logo_promotora = if (esPromotor) logoPromotoraBase64 else null,
            foto_perfil = if (esBoxeador) fotoPerfilBase64 else null
        )

        viewModelScope.launch {
            RetrofitClient.apiService.registrarUsuario(usuario).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        // Si el servidor responde con un error, puedes incluir el mensaje de error
                        val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                        onFailure("Error en el registro: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure("Fallo de conexión: ${t.message}")
                    Log.d("RegistroUsuario", "Error: ${t.message}")
                }
            })
        }
    }
}
