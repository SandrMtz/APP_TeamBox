package com.example.appteambox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.api.RetrofitClient.apiService
import com.example.appteambox.model.Boxeador
import com.example.appteambox.model.FavoritoRequest
import com.example.appteambox.model.FiltrosBusqueda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusquedaBoxeadorViewModel : ViewModel() {

    // Resultados de la búsqueda
    private val _resultados = MutableStateFlow<List<Boxeador>>(emptyList())
    val resultados: StateFlow<List<Boxeador>> = _resultados

    // Mensaje de error
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // Indicador de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Boxeadores seleccionados como favoritos (por su ID)
    private val _favoritosSeleccionados = MutableStateFlow<Set<Int>>(emptySet())
    val favoritosSeleccionados: StateFlow<Set<Int>> = _favoritosSeleccionados

    /**
     * Realiza la búsqueda de boxeadores según los filtros proporcionados.
     */
    fun busquedaBoxeadores(filtros: FiltrosBusqueda) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val resultadosApi = RetrofitClient.apiService.buscarBoxeadores(filtros)
                _resultados.value = resultadosApi
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar boxeadores: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Alterna la selección de un boxeador como favorito.
     */
    fun toggleFavorito(idBoxeador: Int) {
        _favoritosSeleccionados.value = _favoritosSeleccionados.value.let { favoritos ->
            if (favoritos.contains(idBoxeador)) {
                favoritos - idBoxeador
            } else {
                favoritos + idBoxeador
            }
        }
    }

    /**
     * Envía los boxeadores seleccionados como favoritos al backend.
     * @param clubId ID del club que marca como favorito.
     * @param onSuccess Callback en caso de éxito.
     * @param onError Callback en caso de error, recibe mensaje de error.
     */
    fun enviarFavoritos(
        clubId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val favoritos = favoritosSeleccionados.value.map { boxeadorId ->
                    FavoritoRequest(club_id = clubId, boxeador_id = boxeadorId)
                }
                val response = apiService.agregarFavoritos(favoritos)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al guardar favoritos: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Excepción: ${e.localizedMessage}")
            }
        }
    }

}
