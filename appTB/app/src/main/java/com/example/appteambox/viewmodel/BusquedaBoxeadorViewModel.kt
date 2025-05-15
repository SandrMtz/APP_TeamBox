package com.example.appteambox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Boxeador
import com.example.appteambox.model.FiltrosBusqueda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusquedaBoxeadorViewModel : ViewModel() {

    private val _resultados = MutableStateFlow<List<Boxeador>>(emptyList())
    val resultados: StateFlow<List<Boxeador>> = _resultados

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun busquedaBoxeadores(filtros: FiltrosBusqueda) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val resultados = RetrofitClient.apiService.buscarBoxeadores(filtros)
                _resultados.value = resultados
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
