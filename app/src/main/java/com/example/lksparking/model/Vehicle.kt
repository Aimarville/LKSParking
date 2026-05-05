package com.example.lksparking.model

enum class VehicleType {
    AUTOMOBILE, MOTORCYCLE
}

data class Vehicle(
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    val type: VehicleType = VehicleType.AUTOMOBILE,
    val isElectric: Boolean = false,
    val isDisabled: Boolean = false
)
