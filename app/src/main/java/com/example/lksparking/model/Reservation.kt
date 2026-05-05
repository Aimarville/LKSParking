package com.example.lksparking.model

import java.util.Calendar

data class Reservation(
    val id: String,
    val userEmail: String,
    val vehiclePlate: String,
    val spotId: String,
    val startTime: Calendar, // milliseconds
    val endTime: Calendar,   // milliseconds
    val date: Calendar       // milliseconds (normalized to midnight)
)
