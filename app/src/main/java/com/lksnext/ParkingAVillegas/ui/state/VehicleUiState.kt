package com.lksnext.ParkingAVillegas.ui.state

import com.lksnext.ParkingAVillegas.model.Vehicle

data class VehicleUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLonading: Boolean = false,
    val error: String? = null
)