package com.example.appteambox


import com.example.appteambox.api.ApiService
import com.example.appteambox.model.Boxeador
import com.example.appteambox.model.FiltrosBusqueda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BoxeadorRepositorio(private val apiService: ApiService) {

    suspend fun obtenerBoxeadoresPorClub(clubId: Int): List<Boxeador> = withContext(Dispatchers.IO) {
        apiService.getBoxeadoresPorClub(clubId)
    }

    suspend fun agregarBoxeador(boxeador: Boxeador): Unit = withContext(Dispatchers.IO) {
        val response = apiService.crearBoxeador(boxeador)
        response.isSuccessful
    }

    suspend fun editarBoxeador(boxeador: Boxeador): Boolean = withContext(Dispatchers.IO) {
        val response = apiService.editarBoxeador(boxeador.Id_boxeador, boxeador)
        response.isSuccessful
    }

    suspend fun eliminarBoxeador(id: Int): Boolean = withContext(Dispatchers.IO) {
        val response = apiService.eliminarBoxeador(id)
        response.isSuccessful
    }


    suspend fun buscarBoxeadoresConFiltros(filtros: FiltrosBusqueda): List<Boxeador> {
        return apiService.buscarBoxeadores(filtros)
    }
}
