package com.lksnext.ParkingAVillegas.data.repository.vehicle

import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.Vehicle

class VehicleRepositoryImpl(
    private val userRepository: UserRepository
) : VehicleRepository {

    override fun getVehicles(
        email: String
    ): List<Vehicle> {

        return userRepository
            .getUserByEmail(email)
            ?.vehiculos
            ?.toList()
            ?: emptyList()
    }

    override fun addVehicleToUser(
        email: String,
        vehicle: Vehicle
    ): Boolean {

        val exists =
            isPlateRegisteredGlobally(
                vehicle.plate
            )

        if (exists) {
            return false
        }

        val user =
            userRepository.getUserByEmail(email)
                ?: return false

        val updatedUser =
            user.copy(
                vehiculos =
                    user.vehiculos + vehicle
            )

        return userRepository.updateUser(
            updatedUser
        )
    }

    override fun removeVehicleFromUser(
        email: String,
        plate: String
    ): Boolean {

        val user =
            userRepository.getUserByEmail(email)
                ?: return false

        val updatedUser =
            user.copy(
                vehiculos =
                    user.vehiculos.filter {
                        it.plate != plate
                    }
            )

        return userRepository.updateUser(
            updatedUser
        )
    }

    override fun isPlateRegisteredGlobally(
        plate: String
    ): Boolean {

        return userRepository.users.any { user ->

            user.vehiculos.any { vehicle ->

                vehicle.plate.equals(
                    plate,
                    ignoreCase = true
                )
            }
        }
    }
}