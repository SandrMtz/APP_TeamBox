package com.example.appteambox.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appteambox.api.RetrofitClient
import com.example.appteambox.model.Boxeador
import kotlinx.coroutines.launch
import java.time.LocalDate

//Maneja la lógica de Boxeadores, es el intermediario entre la UI y la API
class BoxeadorViewModel : ViewModel() {

    //// Lista interna de boxeadores que puede modificarse
    private val _boxeadores = mutableStateListOf<Boxeador>()

    // Lista pública de solo lectura para que la UI observe
    val boxeadores: SnapshotStateList<Boxeador> = _boxeadores


    // ID del club actual, para cuando recargue siga siendo el mismo
    var clubIdActual: Int = 0

    //Animaciónd de carga
    var cargando by mutableStateOf(false)
        private set

    var mensajeUsuario by mutableStateOf<String?>(null)
        private set

    //Conectamos con el servidor
    private val api = RetrofitClient.apiService


    fun calcularCategoria(fechaNacimiento: String): String {
        // Extraer el año de nacimiento (de los primeros 4 caracteres)
        val anioNacimiento = fechaNacimiento.take(4).toIntOrNull() ?: return "Desconocida"

        // Obtener el año actual
        val anioActual = LocalDate.now().year

        // Calcular la edad
        val edad = anioActual - anioNacimiento

        // Devolver la categoría según la lógica del evento SQL
        return when {
            edad > 18 -> "Élite"
            edad in 17..18 -> "Joven"
            edad in 15..16 -> "Junior"
            edad in 13..14 -> "Cadete"
            edad in 11..12 -> "Infantil"
            edad in 9..10 -> "Benjamin"
            else -> "Prebenjamin"
        }
    }


    // ---------------------------
    // Función privada para comprobar si un DNI ya está registrado
    private suspend fun dniExiste(dni: String, idBoxeador: Int? = null): Boolean {
        return try {
            val existe = api.dniExiste(dni)

            // Si estamos editando, no cuenta como duplicado si es el mismo boxeador
            if (idBoxeador != null) {
                _boxeadores.any { it.dni_boxeador == dni && it.Id_boxeador != idBoxeador }
            } else {
                existe
            }
        } catch (e: Exception) {
            Log.e("BoxeadorViewModel", "Error al consultar dniExiste: ${e.message}", e)
            false
        }
    }

    // ---------------------------
    // Cargar todos los boxeadores de un club específico desde la API
    fun cargarBoxeadores(clubId: Int) {
        clubIdActual = clubId
        viewModelScope.launch {
            cargando = true
            mensajeUsuario = null
            try {
                val resultado = api.getBoxeadoresPorClub(clubId)
                _boxeadores.clear()
                _boxeadores.addAll(resultado)
            } catch (e: Exception) {
                mensajeUsuario = "Error al cargar boxeadores"
                Log.e("BoxeadorViewModel", "Error al cargar boxeadores: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    // ---------------------------
    // Agregar un nuevo boxeador a la BBDD
    fun agregarBoxeador(boxeador: Boxeador) {

        //Valida campos concretos de los datos del boxeador
        if (!validarBoxeador(boxeador)) return

        //Asigna categoria
        val categoria = calcularCategoria(boxeador.fecha_nacimiento)
        val boxeadorConCategoria = boxeador.copy(categoria = categoria)

        viewModelScope.launch {
            // Verificamos si el DNI ya está registrado
            val existe = dniExiste(boxeadorConCategoria.dni_boxeador)
            if (existe) {
                mensajeUsuario = "DNI ya registrado en otro boxeador"
                return@launch
            }

            try {
                // Llamamos al endpoint para guardar el nuevo boxeador
                val response = api.crearBoxeador(boxeadorConCategoria)
                if (response.isSuccessful) {
                    //Recarga la lista de boxeadores
                    cargarBoxeadores(boxeadorConCategoria.club_id)
                    mensajeUsuario = "Boxeador agregado correctamente"
                } else {
                    val errorMsg = response.errorBody()?.string()
                    mensajeUsuario = errorMsg ?: "Error al agregar boxeador"
                    Log.e("BoxeadorViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                mensajeUsuario = "Error al conectar con el servidor"
                Log.e("BoxeadorViewModel", "Error al agregar boxeador: ${e.message}", e)
            }
        }
    }

    // ---------------------------
    // Editar un boxeador ya existente
    fun editarBoxeador(boxeador: Boxeador) {
        if (boxeador.Id_boxeador == 0 || !validarBoxeador(boxeador)) return

        val categoria = calcularCategoria(boxeador.fecha_nacimiento)
        val boxeadorConCategoria = boxeador.copy(categoria = categoria)

        viewModelScope.launch {
            // Comprobamos si el nuevo DNI ya está en uso por otro boxeador
            val dniRepetido = dniExiste(boxeadorConCategoria.dni_boxeador, boxeadorConCategoria.Id_boxeador)
            if (dniRepetido) {
                mensajeUsuario = "DNI ya registrado en otro boxeador"
                return@launch
            }

            try {
                val response = api.editarBoxeador(boxeadorConCategoria.Id_boxeador, boxeadorConCategoria)
                if (response.isSuccessful) {
                    cargarBoxeadores(boxeadorConCategoria.club_id)
                    mensajeUsuario = "Boxeador editado correctamente"
                } else {
                    val errorMsg = response.errorBody()?.string()
                    mensajeUsuario = errorMsg ?: "Error al editar boxeador"
                    Log.e("BoxeadorViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                mensajeUsuario = "Error al conectar con el servidor"
                Log.e("BoxeadorViewModel", "Error al editar boxeador: ${e.message}", e)
            }
        }
    }

    // ---------------------------
    // Eliminar un boxeador según su ID
    fun eliminarBoxeador(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.eliminarBoxeador(id)
                if (response.isSuccessful) {
                    cargarBoxeadores(clubIdActual) // Recargar lista tras eliminación
                    mensajeUsuario = "Boxeador eliminado correctamente"
                } else {
                    val errorMsg = response.errorBody()?.string()
                    mensajeUsuario = errorMsg ?: "Error al eliminar boxeador con ID $id"
                    Log.e("BoxeadorViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                mensajeUsuario = "Error al conectar con el servidor"
                Log.e("BoxeadorViewModel", "Error al eliminar boxeador: ${e.message}", e)
            }
        }
    }



    // ---------------------------
    // Validaciones básicas antes de enviar los datos
    private fun validarBoxeador(boxeador: Boxeador): Boolean {
        return when {
            boxeador.nombre.isBlank() -> {
                mensajeUsuario = "El nombre es obligatorio"
                false
            }
            boxeador.apellido.isBlank() -> {
                mensajeUsuario = "El apellido es obligatorio"
                false
            }
            boxeador.dni_boxeador.isBlank() -> {
                mensajeUsuario = "El DNI es obligatorio"
                false
            }
            boxeador.fecha_nacimiento.isBlank() -> {
                mensajeUsuario = "La fecha de nacimiento es obligatoria"
                false
            }
            boxeador.peso <= 0f -> {
                mensajeUsuario = "El peso debe ser mayor que 0"
                false
            }
            else -> true
        }
    }

    // ---------------------------
    // Limpiar mensaje del usuario (útil al cerrar diálogos o notificaciones)
    fun limpiarMensajeUsuario() {
        mensajeUsuario = null
    }

}
