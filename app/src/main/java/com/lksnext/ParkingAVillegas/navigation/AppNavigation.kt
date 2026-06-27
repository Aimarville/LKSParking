package com.lksnext.ParkingAVillegas.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lksnext.ParkingAVillegas.data.repository.auth.AuthRepositoryImpl
import com.lksnext.ParkingAVillegas.data.repository.reservation.ReservationRepository
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.data.repository.vehicle.VehicleRepository
import com.lksnext.ParkingAVillegas.ui.screens.*
import com.lksnext.ParkingAVillegas.viewmodel.*

@Composable
fun AppNavigation(
    userRepository: UserRepository,
    vehicleRepository: VehicleRepository,
    reservationRepository: ReservationRepository
) {

    var currentScreen by remember {
        mutableStateOf("login")
    }

    var currentUserEmail by remember {
        mutableStateOf<String?>(null)
    }

    val authRepository = remember {
        AuthRepositoryImpl(userRepository)
    }

    val authViewModel = remember {
        AuthViewModel(authRepository)
    }

    val currentUser = authViewModel
        .uiState
        .value
        .loggedUser

    /*
    |--------------------------------------------------------------------------
    | VIEWMODELS
    |--------------------------------------------------------------------------
    */

    val reservationViewModel =
        remember(
            currentUserEmail,
            currentUser // Re-create when user data (like vehicles) changes
        ) {
            currentUserEmail?.let {
                ReservationViewModel(
                    reservationRepository = reservationRepository,
                    userRepository = userRepository,
                    userEmail = it
                )
            }
        }

    val profileViewModel =
        remember(
            currentUserEmail,
            currentUser // Re-create when user data changes
        ) {
            ProfileViewModel(
                userRepository = userRepository,
                userEmail = currentUserEmail ?: ""
            )
        }

    val vehicleViewModel =
        remember(
            currentUserEmail,
            currentUser // Re-create when user data changes
        ) {
            VehicleViewModel(
                vehicleRepository = vehicleRepository,
                userEmail = currentUserEmail ?: ""
            )
        }

    val mainLayoutViewModel =
        remember(
            currentUserEmail,
            currentUser // Re-create when user data changes
        ) {
            MainViewModel(
                user = currentUser
            )
        }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        when (currentScreen) {

            /*
            |--------------------------------------------------------------------------
            | LOGIN
            |--------------------------------------------------------------------------
            */

            "login" -> {

                LoginScreen(
                    viewModel = authViewModel,

                    onForgotPasswordClick = {
                        currentScreen = "forgot_password"
                    },

                    onRegisterClick = {
                        currentScreen = "register"
                    },

                    onLoginSuccess = { user ->

                        currentUserEmail = user.email

                        currentScreen = "my_reservations"
                    }
                )
            }

            /*
            |--------------------------------------------------------------------------
            | FORGOT PASSWORD
            |--------------------------------------------------------------------------
            */

            "forgot_password" -> {

                ForgotPasswordScreen(
                    onBackClick = {
                        currentScreen = "login"
                    }
                )
            }

            /*
            |--------------------------------------------------------------------------
            | REGISTER
            |--------------------------------------------------------------------------
            */

            "register" -> {

                RegisterScreen(
                    viewModel = authViewModel,

                    onLoginClick = {
                        currentScreen = "login"
                    },

                    onRegisterSuccess = {
                        val user =
                            authViewModel
                                .uiState
                                .value
                                .loggedUser

                        if (user != null) {
                            currentUserEmail =
                                user.email

                            currentScreen =
                                "my_reservations"
                        }
                    }
                )
            }

            /*
            |--------------------------------------------------------------------------
            | APP
            |--------------------------------------------------------------------------
            */

            else -> {

                MainLayout(
                    viewModel = mainLayoutViewModel,

                    onNavigate = { route ->

                        if (route == "login") {
                            currentUserEmail = null
                        }

                        currentScreen = route
                    }

                ) { padding ->

                    Box(
                        modifier = Modifier.padding(padding)
                    ) {

                        when (currentScreen) {

                            /*
                            |--------------------------------------------------------------------------
                            | MY RESERVATIONS
                            |--------------------------------------------------------------------------
                            */

                            "my_reservations" -> {

                                reservationViewModel?.let {

                                    MyReservationsScreen(
                                        viewModel = it
                                    )
                                }
                            }

                            /*
                            |--------------------------------------------------------------------------
                            | NEW RESERVATION
                            |--------------------------------------------------------------------------
                            */

                            "new_reservation" -> {

                                reservationViewModel?.let {

                                    NewReservationScreen(
                                        viewModel = it,

                                        onReservationFinished = {
                                            currentScreen =
                                                "my_reservations"
                                        }
                                    )
                                }
                            }

                            /*
                            |--------------------------------------------------------------------------
                            | PROFILE
                            |--------------------------------------------------------------------------
                            */

                            "profile" -> {

                                ProfileScreen(
                                    viewModel = profileViewModel,

                                    onNavigateToVehicles = {
                                        currentScreen =
                                            "my_vehicles"
                                    }
                                )
                            }

                            /*
                            |--------------------------------------------------------------------------
                            | VEHICLES
                            |--------------------------------------------------------------------------
                            */

                            "my_vehicles" -> {

                                MyVehiclesScreen(
                                    viewModel = vehicleViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
