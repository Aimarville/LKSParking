package com.lksnext.ParkingAVillegas.validation

import com.lksnext.ParkingAVillegas.model.*

object VehicleValidator {

    fun validateVehicle(
        plate: String,
        brand: String,
        model: String
    ): ValidationResult {

        if (plate.isBlank()) {
            return ValidationResult(false, "La matrícula es obligatoria")
        }

        if (brand.isBlank()) {
            return ValidationResult(false, "La marca es obligatoria")
        }

        if (model.isBlank()) {
            return ValidationResult(false, "El modelo es obligatorio")
        }

        return ValidationResult(true)
    }

    fun validateVehicleForSpot(
        vehicle: Vehicle,
        spotType: SpotType
    ): ValidationResult {

        val valid = when (spotType) {

            SpotType.NORMAL ->
                vehicle.type == VehicleType.AUTOMOBILE

            SpotType.DISABLED ->
                vehicle.type == VehicleType.AUTOMOBILE &&
                        vehicle.isDisabled

            SpotType.ELECTRIC ->
                vehicle.type == VehicleType.AUTOMOBILE &&
                        vehicle.isElectric

            SpotType.MOTORCYCLE ->
                vehicle.type == VehicleType.MOTORCYCLE
        }

        if (!valid) {

            val error = when (spotType) {
                SpotType.DISABLED ->
                    "Solo vehículos para minusválidos pueden usar esta plaza"

                SpotType.ELECTRIC ->
                    "Solo vehículos eléctricos pueden usar esta plaza"

                SpotType.MOTORCYCLE ->
                    "Solo motocicletas pueden usar esta plaza"

                SpotType.NORMAL ->
                    "Vehículo no válido para esta plaza"
            }

            return ValidationResult(false, error)
        }

        return ValidationResult(true)
    }
}