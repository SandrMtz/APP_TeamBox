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

    private val _favoritos = MutableStateFlow<List<Boxeador>>(emptyList())
    val favoritos: StateFlow<List<Boxeador>> = _favoritos

    // Set de IDs seleccionados para eliminar
    var boxeadoresSeleccionados = mutableStateListOf<Int>()

    fun toggleSeleccion(idBoxeador: Int) {
        if (boxeadoresSeleccionados.contains(idBoxeador)) {
            boxeadoresSeleccionados.remove(idBoxeador)
        } else {
            boxeadoresSeleccionados.add(idBoxeador)
        }
    }

    fun obtenerFavoritosPorClub(clubId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerFavoritos(clubId)
                }
                _favoritos.value = response
            } catch (e: Exception) {
                Log.e("FavoritosViewModel", "Error al obtener favoritos", e)
            }
        }
    }

    fun eliminarFavoritosSeleccionados(clubId: Int) {
        viewModelScope.launch {
            val idsAEliminar = boxeadoresSeleccionados.toList()
            for (idBoxeador in idsAEliminar) {
                try {
                    withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.eliminarFavorito(clubId, idBoxeador)
                    }
                } catch (e: Exception) {
                    Log.e("FavoritosViewModel", "Error eliminando favorito ID $idBoxeador", e)
                }
            }
            boxeadoresSeleccionados.clear()
            obtenerFavoritosPorClub(clubId)
        }
    }
}