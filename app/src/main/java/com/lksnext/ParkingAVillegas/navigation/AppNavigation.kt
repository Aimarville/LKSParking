package com.lksnext.ParkingAVillegas.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.lksnext.ParkingAVillegas.data.repository.AuthRepository
import com.lksnext.ParkingAVillegas.data.repository.AuthRepositoryImpl
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.data.static.ParkingData
import com.lksnext.ParkingAVillegas.ui.screens.*
import com.lksnext.ParkingAVillegas.viewmodel.AuthViewModel
import com.lksnext.ParkingAVillegas.viewmodel.ProfileViewModel
import com.lksnext.ParkingAVillegas.viewmodel.VehicleViewModel

@Composable
fun AppNavigation(
    userRepository: UserRepository
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

    val userActual = userRepository
        .usersState
        .find { it.email == currentUserEmail }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        when (currentScreen) {

            // LOGIN
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

            // FORGOT PASSWORD
            "forgot_password" -> {

                ForgotPasswordScreen(
                    onBackClick = {
                        currentScreen = "login"
                    }
                )
            }

            // REGISTER
            "register" -> {

                RegisterScreen(
                    viewModel = authViewModel,

                    onLoginClick = {
                        currentScreen = "login"
                    },

                    onRegisterSuccess = {
                        currentScreen = "login"
                    }
                )
            }

            else -> {

                MainLayout(
                    currentUser = userActual,
                    currentRoute = currentScreen,

                    onNavigate = {

                        if (it == "login") {
                            currentUserEmail = null
                        }

                        currentScreen = it
                    }

                ) { padding ->

                    Box(
                        modifier = Modifier.padding(padding)
                    ) {

                        when (currentScreen) {

                            // RESERVATIONS
                            "my_reservations" -> {

                                MyReservationsScreen(
                                    currentUser = userActual,
                                    allReservations = userRepository.reservationsState,
                                    allParkingSpots = ParkingData.allSpots,
                                    userRepository = userRepository
                                )
                            }

                            // NEW RESERVATION
                            "new_reservation" -> {

                                NewReservationScreen(
                                    user = userActual,
                                    userRepository = userRepository,

                                    onReservationFinished = {
                                        currentScreen = "my_reservations"
                                    }
                                )
                            }

                            // PROFILE
                            "profile" -> {

                                val profileViewModel = remember(currentUserEmail) {

                                    ProfileViewModel(
                                        repository = userRepository,
                                        userEmail = currentUserEmail ?: ""
                                    )
                                }

                                ProfileScreen(
                                    viewModel = profileViewModel,

                                    onNavigateToVehicles = {
                                        currentScreen = "my_vehicles"
                                    }
                                )
                            }

                            // VEHICLES
                            "my_vehicles" -> {

                                val vehicleViewModel = remember(currentUserEmail) {

                                    VehicleViewModel(
                                        repository = userRepository,
                                        userEmail = currentUserEmail ?: ""
                                    )
                                }

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