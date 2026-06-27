package com.lksnext.ParkingAVillegas.ui.state

import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import java.util.Calendar

data class ReservationUiState(
    val user: User? = null,

    // LISTADOS
    val currentReservations: List<Reservation> = emptyList(),
    val historicalReservations: List<Reservation> = emptyList(),

    // NUEVA RESERVA
    val currentStep: Int = 1,

    val selectedVehicle: Vehicle? = null,
    val selectedDate: Calendar? = null,
    val startTime: Calendar? = null,
    val endTime: Calendar? = null,

    val selectedSpot: ParkingSpot? = null,

    val availableSpots: List<ParkingSpot> = emptyList(),

    val filterType: String = "TODAS",

    // UI
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val updateSuccess: Boolean = false,
    val error: String? = null
)