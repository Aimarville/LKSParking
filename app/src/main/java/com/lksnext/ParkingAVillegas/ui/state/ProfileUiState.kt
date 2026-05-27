package com.lksnext.ParkingAVillegas.ui.state

import android.net.Uri

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val photoUri: Uri? = null,
    val vehicleCount: Int = 0,
    val isEditing: Boolean = false,
    val success: Boolean = false,
)