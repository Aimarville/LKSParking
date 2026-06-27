package com.lksnext.ParkingAVillegas.data.repository.vehicle

import com.google.firebase.firestore.FirebaseFirestore
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.Vehicle
import kotlinx.coroutines.tasks.await

class VehicleRepositoryImpl(
    private val userRepository: UserRepository
) : VehicleRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    override suspend fun getVehicles(
        email: String
    ): List<Vehicle> {

        return userRepository
            .getUserByEmail(email)
            ?.vehiculos
            ?: emptyList()
    }

    override suspend fun addVehicleToUser(
        email: String,
        vehicle: Vehicle
    ): Boolean {

        if (
            isPlateRegisteredGlobally(
                vehicle.plate
            )
        ) {
            return false
        }

        val user =
            userRepository.getUserByEmail(email)
                ?: return false

        val updatedVehicles =
            user.vehiculos + vehicle

        firestore
            .collection("users")
            .document(user.uid)
            .update(
                "vehiculos",
                updatedVehicles
            )
            .await()

        return true
    }

    override suspend fun removeVehicleFromUser(
        email: String,
        plate: String
    ): Boolean {

        val user =
            userRepository.getUserByEmail(email)
                ?: return false

        val updatedVehicles =
            user.vehiculos.filter {
                it.plate != plate
            }

        firestore
            .collection("users")
            .document(user.uid)
            .update(
                "vehiculos",
                updatedVehicles
            )
            .await()

        return true
    }

    override suspend fun isPlateRegisteredGlobally(
        plate: String
    ): Boolean {

        val users =
            firestore
                .collection("users")
                .get()
                .await()

        return users.documents.any { document ->

            val user =
                document.toObject(
                    com.lksnext.ParkingAVillegas.model.User::class.java
                )

            user?.vehiculos?.any {
                it.plate.equals(
                    plate,
                    ignoreCase = true
                )
            } == true
        }
    }
}