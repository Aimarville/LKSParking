package com.lksnext.ParkingAVillegas.data.repository.vehicle

import com.lksnext.ParkingAVillegas.model.Vehicle

interface VehicleRepository {

    suspend fun getVehicles(
        email: String
    ): List<Vehicle>

    suspend fun addVehicleToUser(
        email: String,
        vehicle: Vehicle
    ): Boolean

    suspend fun removeVehicleFromUser(
        email: String,
        plate: String
    ): Boolean

    suspend fun isPlateRegisteredGlobally(
        plate: String
    ): Boolean
}