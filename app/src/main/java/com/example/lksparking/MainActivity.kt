package com.example.lksparking

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
import com.example.lksparking.data.UserRepository
import com.example.lksparking.ui.screens.*
import com.example.lksparking.ui.theme.LKSParkingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LKSParkingTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("login") }
    var currentUserEmail by remember { mutableStateOf<String?>(null) } // Guardamos solo el Email

    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }

    // Buscamos al usuario en tiempo real desde el estado reactivo del repositorio
    val userActual = userRepository.usersState.find { it.email == currentUserEmail }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "login" -> LoginScreen(
                userRepository = userRepository,
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onRegisterClick = { currentScreen = "register" },
                onLoginSuccess = { user ->
                    currentUserEmail = user.email // Guardamos el email
                    currentScreen = "my_reservations"
                }
            )

            // ... forgot_password y register se mantienen igual ...

            else -> {
                MainLayout(
                    // Pasamos userActual para que la barra lateral/superior vea los cambios (como la foto)
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
                                allReservations = userRepository.reservationsState // Usamos el estado reactivo
                            )
                            "new_reservation" -> NewReservationScreen(
                                user = userActual,
                                userRepository = userRepository,
                                onReservationFinished = {
                                    currentScreen = "my_reservations"
                                }
                            )
                            "profile" -> ProfileScreen(
                                userEmail = currentUserEmail ?: "", // Pasamos el email
                                userRepository = userRepository,
                                onNavigateToVehicles = { currentScreen = "my_vehicles" }
                            )
                            "my_vehicles" -> MyVehiclesScreen(
                                user = userActual, // Pasamos el usuario actualizado
                                userRepository = userRepository
                            )
                        }
                    }
                }
            }
        }
    }
}