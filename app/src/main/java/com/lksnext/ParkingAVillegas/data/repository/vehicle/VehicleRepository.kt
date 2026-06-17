package com.lksnext.ParkingAVillegas.data.repository.vehicle

import com.lksnext.ParkingAVillegas.model.Vehicle

interface VehicleRepository {

    fun getVehicles(
        email: String
    ): List<Vehicle>

    fun addVehicleToUser(
        email: String,
        vehicle: Vehicle
    ): Boolean

    fun removeVehicleFromUser(
        email: String,
        plate: String
    ): Boolean

    fun isPlateRegisteredGlobally(
        plate: String
    ): Boolean
}