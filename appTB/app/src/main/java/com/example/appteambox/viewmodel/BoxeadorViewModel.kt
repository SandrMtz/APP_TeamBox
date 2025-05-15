package com.example.appteambox.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Boxeador
import com.example.appteambox.ui.screens.PantallaBoxeadores
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel

class BoxeadorViewModel : ViewModel() {

    private val _boxeadores = mutableStateListOf<Boxeador>()
    val boxeadores: List<Boxeador> get() = _boxeadores

    var cargando by mutableStateOf(false)
        private set

    private val api = RetrofitClient.apiService

    // Función para consultar si un DNI ya existe en la base de datos
    private suspend fun dniExiste(dni: String): Boolean {
        return try {
            api.dniExiste(dni)
        } catch (e: Exception) {
            Log.e("BoxeadorViewModel", "Error al consultar dniExiste: ${e.message}", e)
            false
        }
    }

    fun cargarBoxeadores(clubId: Int) {
        viewModelScope.launch {
            cargando = true
            try {
                val resultado = api.getBoxeadoresPorClub(clubId)
                _boxeadores.clear()
                _boxeadores.addAll(resultado)
            } catch (e: Exception) {
                Log.e("BoxeadorViewModel", "Error al cargar boxeadores: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    fun agregarBoxeador(boxeador: Boxeador) {
        if (boxeador.nombre.isBlank() || boxeador.apellido.isBlank()) {
            Log.w("BoxeadorViewModel", "Nombre o apellido vacíos, no se agrega boxeador")
            return
        }
        if (boxeador.dni_boxeador.isBlank()) {
            Log.w("BoxeadorViewModel", "DNI vacío, no se agrega boxeador")
            return
        }

        viewModelScope.launch {
            val existe = dniExiste(boxeador.dni_boxeador)
            if (existe) {
                Log.w("BoxeadorViewModel", "DNI ya existe en la base de datos, no se agrega")
                return@launch
            }

            try {
                val response = api.crearBoxeador(boxeador)
                if (response.isSuccessful) {
                    cargarBoxeadores(boxeador.club_id)
                } else {
                    Log.e("BoxeadorViewModel", "Error en respuesta al agregar boxeador: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BoxeadorViewModel", "Error al agregar boxeador: ${e.message}", e)
            }
        }
    }

    fun editarBoxeador(boxeador: Boxeador) {
        if (boxeador.Id_boxeador == 0) {
            Log.w("BoxeadorViewModel", "ID de boxeador inválido para editar")
            return
        }
        if (boxeador.dni_boxeador.isBlank()) {
            Log.w("BoxeadorViewModel", "DNI vacío, no se edita boxeador")
            return
        }

        viewModelScope.launch {
            val existe = dniExiste(boxeador.dni_boxeador)
            val dniRepetido = existe && _boxeadores.any { it.dni_boxeador == boxeador.dni_boxeador && it.Id_boxeador != boxeador.Id_boxeador }

            if (dniRepetido) {
                Log.w("BoxeadorViewModel", "DNI ya existe en otro boxeador, no se edita")
                return@launch
            }

            try {
                val response = api.editarBoxeador(boxeador.Id_boxeador, boxeador)
                if (response.isSuccessful) {
                    cargarBoxeadores(boxeador.club_id)
                } else {
                    Log.e("BoxeadorViewModel", "Error en respuesta al editar boxeador: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BoxeadorViewModel", "Error al editar boxeador: ${e.message}", e)
            }
        }
    }

    fun eliminarBoxeador(id: Int, club_id: Int) {
        if (id == 0) {
            Log.w("BoxeadorViewModel", "ID inválido para eliminar boxeador")
            return
        }
        viewModelScope.launch {
            try {
                val response = api.eliminarBoxeador(id)
                if (response.isSuccessful) {
                    cargarBoxeadores(club_id)
                } else {
                    Log.e("BoxeadorViewModel", "Error en respuesta al eliminar boxeador: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BoxeadorViewModel", "Error al eliminar boxeador: ${e.message}", e)
            }
        }
    }
}

@Composable
fun PantallaBoxeadoresConViewModel(
    club_id: Int,
    navController: NavController,
    viewModel: BoxeadorViewModel = composeViewModel()
) {
    LaunchedEffect(key1 = club_id) {
        viewModel.cargarBoxeadores(club_id)
    }

    val listaBoxeadores = viewModel.boxeadores

    PantallaBoxeadores(
        listaBoxeadores = listaBoxeadores,
        onAgregar = { nuevoBoxeador -> viewModel.agregarBoxeador(nuevoBoxeador) },
        onEditar = { boxeadorEditado -> viewModel.editarBoxeador(boxeadorEditado) },
        onEliminar = { boxeadorAEliminar -> viewModel.eliminarBoxeador(boxeadorAEliminar.Id_boxeador, club_id) },
        navController = navController
    )
}
