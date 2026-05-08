package com.lksnext.ParkingAVillegas.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar

class UserRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val usersFileName = "users_v2.json"
    private val resFileName = "reservations_v5.json" // Incrementamos versión para asegurar limpieza
    private val usersFile = File(context.filesDir, usersFileName)
    private val resFile = File(context.filesDir, resFileName)

    val usersState = mutableStateListOf<User>()
    val reservationsState = mutableStateListOf<Reservation>()

    init {
        loadData()
    }

    private fun loadData() {
        val loadedUsers = loadUsersFromFile()
        val loadedRes = loadReservationsFromFile()

        usersState.clear()
        usersState.addAll(loadedUsers)

        reservationsState.clear()
        reservationsState.addAll(loadedRes)
        Log.d("UserRepository", "Cargadas ${reservationsState.size} reservas")
    }

    private fun loadUsersFromFile(): List<User> {
        if (!usersFile.exists()) return emptyList()
        return try {
            val json = usersFile.readText()
            gson.fromJson(json, object : TypeToken<List<User>>() {}.type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun saveAllUsers() {
        try { usersFile.writeText(gson.toJson(usersState.toList())) } catch (e: Exception) { }
    }

    fun saveUser(user: User): Boolean {
        if (usersState.any { it.email == user.email }) return false
        usersState.add(user)
        saveAllUsers()
        return true
    }

    private fun loadReservationsFromFile(): List<Reservation> {
        if (!resFile.exists()) return emptyList()
        return try {
            val json = resFile.readText()
            gson.fromJson<List<Reservation>>(json, object : TypeToken<List<Reservation>>() {}.type) ?: emptyList()
        } catch (e: Exception) { 
            Log.e("UserRepository", "Error cargando reservas: ${e.message}")
            emptyList() 
        }
    }

    private fun saveAllReservations() {
        try { 
            val json = gson.toJson(reservationsState.toList())
            resFile.writeText(json)
            Log.d("UserRepository", "Reservas guardadas en archivo")
        } catch (e: Exception) { 
            Log.e("UserRepository", "Error guardando reservas: ${e.message}")
        }
    }

    fun addReservation(reservation: Reservation): Boolean {
        if (isSpotAvailable(reservation.spotId, reservation.date, reservation.startTime, reservation.endTime)) {
            reservationsState.add(reservation)
            saveAllReservations()
            return true
        }
        return false
    }

    fun deleteReservation(reservationId: String) {
        reservationsState.removeAll { it.id == reservationId }
        saveAllReservations()
    }

    fun updateReservation(updatedRes: Reservation): Boolean {
        if (isSpotAvailable(updatedRes.spotId, updatedRes.date, updatedRes.startTime, updatedRes.endTime, excludeId = updatedRes.id)) {
            val index = reservationsState.indexOfFirst { it.id == updatedRes.id }
            if (index != -1) {
                reservationsState[index] = updatedRes
                saveAllReservations()
                return true
            }
        }
        return false
    }

    fun isSpotAvailable(spotId: String, dateMs: Long, startMs: Long, endMs: Long, excludeId: String? = null): Boolean {
        return reservationsState.none { res ->
            if (excludeId != null && res.id == excludeId) return@none false
            
            if (res.spotId == spotId) {
                // Comprobamos si es el mismo día (normalizado a medianoche)
                val cal1 = Calendar.getInstance().apply { timeInMillis = res.date }
                val cal2 = Calendar.getInstance().apply { timeInMillis = dateMs }
                val sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                             cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

                if (sameDay) {
                    // Hay solapamiento si: nuevo_inicio < res_fin Y nuevo_fin > res_inicio
                    startMs < res.endTime && endMs > res.startTime
                } else false
            } else false
        }
    }

    fun getUserByEmail(email: String): User? = usersState.find { it.email == email }
    
    fun updateProfile(email: String, newName: String, newPhone: String, newDept: String, photoUri: String?): Boolean {
        val index = usersState.indexOfFirst { it.email == email }
        if (index != -1) {
            usersState[index] = usersState[index].copy(nombre = newName, telefono = newPhone, departamento = newDept, profilePhotoUri = photoUri)
            saveAllUsers()
            return true
        }
        return false
    }

    fun addVehicleToUser(email: String, vehicle: Vehicle): Boolean {
        val index = usersState.indexOfFirst { it.email == email }
        if (index != -1) {
            val updatedVehicles = usersState[index].vehiculos.toMutableList().apply { add(vehicle) }
            usersState[index] = usersState[index].copy(vehiculos = updatedVehicles)
            saveAllUsers()
            return true
        }
        return false
    }

    fun removeVehicleFromUser(email: String, plate: String): Boolean {
        val index = usersState.indexOfFirst { it.email == email }
        if (index != -1) {
            val updatedVehicles = usersState[index].vehiculos.filter { it.plate != plate }
            usersState[index] = usersState[index].copy(vehiculos = updatedVehicles)
            saveAllUsers()
            return true
        }
        return false
    }

    fun isPlateRegisteredGlobally(plate: String): Boolean {
        return usersState.any { u -> u.vehiculos.any { v -> v.plate.equals(plate, true) } }
    }
}
