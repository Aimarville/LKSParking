package com.lksnext.ParkingAVillegas.model

data class Reservation(
    val id: String,
    val userEmail: String,
    val vehiclePlate: String,
    val spotId: String,
    val startTime: Long, // Milisegundos
    val endTime: Long,   // Milisegundos
    val date: Long       // Milisegundos (medianoche)
)
