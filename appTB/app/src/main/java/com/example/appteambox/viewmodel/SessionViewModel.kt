package com.example.appteambox.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val _idUsuario = MutableStateFlow(-1)
    val idUsuario: StateFlow<Int> = _idUsuario

    init {
        loadUserId()
    }

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

    fun clearUserId() {
        _idUsuario.value = -1
        val context = getApplication<Application>().applicationContext
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("id_usuario").apply()
    }
}