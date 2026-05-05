package com.example.lksparking.model

enum class SpotType {
    NORMAL, DISABLED, ELECTRIC, MOTORCYCLE
}

data class ParkingSpot(
    val id: String,
    val type: SpotType
)
