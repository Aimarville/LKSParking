package com.example.lksparking.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.lksparking.model.User
import com.example.lksparking.model.Reservation
import com.example.lksparking.model.Vehicle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar

class UserRepository(private val context: Context) {
    private val gson = Gson()
    private val usersFileName = "users.json"
    private val resFileName = "reservations.json"
    private val usersFile = File(context.filesDir, usersFileName)
    private val resFile = File(context.filesDir, resFileName)

    val usersState = mutableStateListOf<User>()
    val reservationsState = mutableStateListOf<Reservation>()

    init {
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

    private fun saveAllUsers(users: List<User>) {
        try {
            usersFile.writeText(gson.toJson(users))
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun saveUser(user: User): Boolean {
        if (usersState.any { it.email == user.email }) return false
        usersState.add(user)
        saveAllUsers(usersState.toList())
        return true
    }

    // ACTUALIZADO: Función para editar datos del perfil y foto
    fun updateProfile(email: String, newName: String, newPhone: String, newDept: String, photoUri: String?): Boolean {
        val index = usersState.indexOfFirst { it.email == email }
        if (index != -1) {
            val user = usersState[index]
            usersState[index] = user.copy(
                nombre = newName,
                telefono = newPhone,
                departamento = newDept,
                profilePhotoUri = photoUri // Asegúrate de añadir este campo a tu data class User
            )
            saveAllUsers(usersState.toList()) // PERSISTENCIA
            return true
        }
        return false
    }

    // ACTUALIZADO: Ahora guarda en el JSON tras añadir vehículo
    fun addVehicleToUser(email: String, vehicle: Vehicle): Boolean {
        val userIndex = usersState.indexOfFirst { it.email == email }
        if (userIndex != -1) {
            val user = usersState[userIndex]
            val newVehicles = user.vehiculos.toMutableList().apply { add(vehicle) }
            usersState[userIndex] = user.copy(vehiculos = newVehicles)
            saveAllUsers(usersState.toList()) // PERSISTENCIA
            return true
        }
        return false
    }

    // ACTUALIZADO: Ahora guarda en el JSON tras eliminar vehículo
    fun removeVehicleFromUser(email: String, plate: String): Boolean {
        val userIndex = usersState.indexOfFirst { it.email == email }
        if (userIndex != -1) {
            val user = usersState[userIndex]
            val newVehicles = user.vehiculos.filter { it.plate != plate }
            usersState[userIndex] = user.copy(vehiculos = newVehicles)
            saveAllUsers(usersState.toList()) // PERSISTENCIA
            return true
        }
        return false
    }

    // --- LÓGICA DE RESERVAS ---
    private fun loadReservationsFromFile(): List<Reservation> {
        if (!resFile.exists()) return emptyList()
        return try {
            val json = resFile.readText()
            val type = object : TypeToken<List<Reservation>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun saveAllReservations() {
        try {
            resFile.writeText(gson.toJson(reservationsState.toList()))
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun addReservation(reservation: Reservation) {
        reservationsState.add(reservation)
        saveAllReservations()
    }

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

    fun getUserByEmail(email: String): User? = usersState.find { it.email == email }

    fun isPlateRegisteredGlobally(plate: String): Boolean {
        return usersState.any { user ->
            user.vehiculos.any { it.plate.equals(plate, ignoreCase = true) }
        }
    }
}