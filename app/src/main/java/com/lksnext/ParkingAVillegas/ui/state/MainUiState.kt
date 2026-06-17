package com.lksnext.ParkingAVillegas.ui.state

import com.lksnext.ParkingAVillegas.model.User

data class MainUiState(
    val currentRoute: String = "my_reservations",
    val currentUser: User? = null,
    val showNotifications: Boolean = false,
    val notificationCount: Int = 2
)