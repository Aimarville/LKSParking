package com.lksnext.ParkingAVillegas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.lksnext.ParkingAVillegas.data.ParkingData
import com.lksnext.ParkingAVillegas.data.UserRepository
import com.lksnext.ParkingAVillegas.ui.screens.*
import com.lksnext.ParkingAVillegas.ui.theme.LKSParkingTheme

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(applicationContext)
        enableEdgeToEdge()
        setContent {
            LKSParkingTheme {
                AppNavigation(userRepository)
            }
        }
    }
}

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