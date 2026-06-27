package com.lksnext.ParkingAVillegas.model

data class Reservation(
    val id: String = "",
    val userEmail: String = "",
    val vehiclePlate: String = "",
    val spotId: String = "",
    val startTime: Long = 0L, // Milisegundos
    val endTime: Long = 0L,   // Milisegundos
    val date: Long = 0L       // Milisegundos (medianoche)
)
