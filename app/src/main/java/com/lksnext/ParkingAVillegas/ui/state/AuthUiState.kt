package com.lksnext.ParkingAVillegas.ui.state

import com.lksnext.ParkingAVillegas.model.User

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedUser: User? = null,
    val isLogged: Boolean = false,
    val registerSuccess: Boolean = false
)