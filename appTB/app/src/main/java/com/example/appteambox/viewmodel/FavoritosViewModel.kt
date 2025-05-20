package com.example.appteambox.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Boxeador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosViewModel : ViewModel() {

    // Lista observable de boxeadores favoritos obtenida desde la API
    private val _favoritos = MutableStateFlow<List<Boxeador>>(emptyList())
    val favoritos: StateFlow<List<Boxeador>> = _favoritos

    // Lista reactiva de IDs de boxeadores seleccionados para eliminar
    var boxeadoresSeleccionados = mutableStateListOf<Int>()

    // Alterna la selección de un boxeador: si está seleccionado lo quita, si no lo añade
    fun toggleSeleccion(idBoxeador: Int) {
        if (boxeadoresSeleccionados.contains(idBoxeador)) {
            boxeadoresSeleccionados.remove(idBoxeador)
        } else {
            boxeadoresSeleccionados.add(idBoxeador)
        }
    }

    // Llama a la API para obtener los boxeadores favoritos de un club específico
    fun obtenerFavoritosPorClub(clubId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // Llamada en hilo de I/O para evitar bloquear la UI
                    RetrofitClient.apiService.obtenerFavoritos(clubId)
                }
                // Se actualiza la lista observable
                _favoritos.value = response
            } catch (e: Exception) {
                Log.e("FavoritosViewModel", "Error al obtener favoritos", e)
            }
        }
    }

    // Elimina todos los favoritos seleccionados del club actual
    fun eliminarFavoritosSeleccionados(clubId: Int) {
        viewModelScope.launch {
            //Se duplica la lista por si hay conflicto
            val idsAEliminar = boxeadoresSeleccionados.toList()
            for (idBoxeador in idsAEliminar) {
                try {
                    withContext(Dispatchers.IO) {
                        // Llamada a la API para eliminar un favorito específico
                        RetrofitClient.apiService.eliminarFavorito(clubId, idBoxeador)
                    }
                } catch (e: Exception) {
                    Log.e("FavoritosViewModel", "Error eliminando favorito ID $idBoxeador", e)
                }
            }
            // Limpiamos la selección después de eliminar
            boxeadoresSeleccionados.clear()
            // Recargamos los favoritos actualizados desde el servidor
            obtenerFavoritosPorClub(clubId)
        }
    }
}