package com.example.lksparking.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.lksparking.model.User
import com.example.lksparking.model.Reservation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar

class UserRepository(private val context: Context) {
    private val gson = Gson()

    // Archivos
    private val usersFileName = "users.json"
    private val resFileName = "reservations.json"

    private val usersFile = File(context.filesDir, usersFileName)
    private val resFile = File(context.filesDir, resFileName)

    // Estados reactivos para Compose
    val usersState = mutableStateListOf<User>()
    val reservationsState = mutableStateListOf<Reservation>()

    init {
        // Cargar datos al iniciar la app
        usersState.addAll(loadUsersFromFile())
        reservationsState.addAll(loadReservationsFromFile())
    }

    // --- LÓGICA DE USUARIOS ---
    private fun loadUsersFromFile(): List<User> {
        if (!usersFile.exists()) return emptyList()
        return try {
            val json = usersFile.readText()
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun getUsers(): List<User> = loadUsersFromFile()

    fun updateUser(updatedUser: User): Boolean {
        val index = usersState.indexOfFirst { it.email == updatedUser.email }
        if (index != -1) {
            usersState[index] = updatedUser
            saveAllUsers(usersState.toList())
            return true
        }
        return false
    }

    fun saveUser(user: User): Boolean {
        // 1. Verificar si ya existe en la lista de memoria (que es el reflejo fiel del JSON)
        if (usersState.any { it.email == user.email }) {
            return false
        }

        // 2. Añadir a la lista reactiva (esto actualiza la UI automáticamente)
        usersState.add(user)

        // 3. Persistir la lista completa actualizada al archivo JSON
        saveAllUsers(usersState.toList())

        return true
    }

    private fun saveAllUsers(users: List<User>) {
        try {
            usersFile.writeText(gson.toJson(users))
        } catch (e: Exception) { e.printStackTrace() }
    }

    // --- LÓGICA DE RESERVAS (NUEVA) ---
    private fun loadReservationsFromFile(): List<Reservation> {
        if (!resFile.exists()) return emptyList()
        return try {
            val json = resFile.readText()
            val type = object : TypeToken<List<Reservation>>() {}.type
            // Importante: Gson necesita adaptadores especiales si usas Calendar,
            // pero para objetos simples funciona bien.
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun saveAllReservations() {
        try {
            resFile.writeText(gson.toJson(reservationsState.toList()))
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun addReservation(reservation: Reservation) {
        reservationsState.add(reservation) // Actualiza la UI
        saveAllReservations() // Guarda en el archivo .json
    }

    fun getAllReservations(): List<Reservation> = reservationsState

    // --- VALIDACIÓN DE DISPONIBILIDAD ---
    fun isSpotAvailable(spotId: String, date: Calendar, start: Calendar, end: Calendar): Boolean {
        return reservationsState.none { res ->
            val sameDay = res.date.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                    res.date.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

            if (res.spotId == spotId && sameDay) {
                val overlap = start.timeInMillis < res.endTime.timeInMillis &&
                        end.timeInMillis > res.startTime.timeInMillis
                overlap
            } else false
        }
    }

    // Otros métodos existentes...
    fun getUserByEmail(email: String): User? = usersState.find { it.email == email }

    fun isPlateRegisteredGlobally(plate: String): Boolean {
        return usersState.any { user ->
            user.vehiculos.any { it.plate.equals(plate, ignoreCase = true) }
        }
    }
}