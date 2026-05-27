package com.lksnext.ParkingAVillegas.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.lksnext.ParkingAVillegas.data.static.ParkingData
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.ui.screens.ForgotPasswordScreen
import com.lksnext.ParkingAVillegas.ui.screens.LoginScreen
import com.lksnext.ParkingAVillegas.ui.screens.MainLayout
import com.lksnext.ParkingAVillegas.ui.screens.MyReservationsScreen
import com.lksnext.ParkingAVillegas.ui.screens.MyVehiclesScreen
import com.lksnext.ParkingAVillegas.ui.screens.NewReservationScreen
import com.lksnext.ParkingAVillegas.ui.screens.ProfileScreen
import com.lksnext.ParkingAVillegas.ui.screens.RegisterScreen

@Composable
fun AppNavigation(userRepository: UserRepository) {
    var currentScreen by remember { mutableStateOf("login") }
    var currentUserEmail by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    // Buscamos al usuario en tiempo real.
    // Al ser usersState un mutableStateListOf, cualquier cambio en el perfil o vehículos
    // provocará que userActual se actualice y todas las pantallas se refresquen.
    val userActual = userRepository.usersState.find { it.email == currentUserEmail }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "login" -> LoginScreen(
                userRepository = userRepository,
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onRegisterClick = { currentScreen = "register" },
                onLoginSuccess = { user ->
                    currentUserEmail = user.email
                    currentScreen = "my_reservations"
                }
            )

            "forgot_password" -> ForgotPasswordScreen(
                onBackClick = { currentScreen = "login" }
            )

            "register" -> RegisterScreen(
                userRepository = userRepository,
                onLoginClick = { currentScreen = "login" },
                onRegisterSuccess = {
                    currentScreen = "login"
                }
            )

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
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            "my_reservations" -> MyReservationsScreen(
                                currentUser = userActual,
                                allReservations = userRepository.reservationsState,
                                allParkingSpots = ParkingData.allSpots,
                                userRepository = userRepository
                            )
                            "new_reservation" -> NewReservationScreen(
                                user = userActual,
                                userRepository = userRepository,
                                onReservationFinished = {
                                    currentScreen = "my_reservations"
                                }
                            )
                            "profile" -> ProfileScreen(
                                userEmail = currentUserEmail ?: "",
                                userRepository = userRepository,
                                onNavigateToVehicles = { currentScreen = "my_vehicles" }
                            )
                            "my_vehicles" -> MyVehiclesScreen(
                                user = userActual,
                                userRepository = userRepository
                            )
                        }
                    }
                }
            }
        }
    }
}