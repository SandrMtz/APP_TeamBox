package com.example.appteambox.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel que mantiene la sesión del usuario y su ID persistente en SharedPreferences
class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // Estado interno para el ID de usuario, inicialmente -1 (no logueado)
    private val _idUsuario = MutableStateFlow(-1)
    // Exposición solo lectura del ID de usuario como StateFlow
    val idUsuario: StateFlow<Int> = _idUsuario

    // Inicialización: carga el ID de usuario almacenado en preferencias
    init {
        loadUserId()
    }

    // Función privada para cargar el ID desde SharedPreferences de forma asíncrona
    private fun loadUserId() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val id = prefs.getInt("id_usuario", -1)
            _idUsuario.value = id
        }
    }

    // Si quieres actualizar el ID (por ejemplo, al hacer login o logout)
    fun setUserId(id: Int) {
        _idUsuario.value = id
        val context = getApplication<Application>().applicationContext
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("id_usuario", id).apply()
    }

    /**
     * Limpia el ID de usuario en el estado y en SharedPreferences.
     * Usado para cerrar sesión o eliminar datos guardados.
     */

    fun clearUserId() {
        _idUsuario.value = -1
        val context = getApplication<Application>().applicationContext
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("id_usuario").apply()
    }
}