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
import com.example.lksparking.model.User
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
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }

    val userActual = userRepository.usersState.find { it.email == currentUser?.email }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "login" -> LoginScreen(
                userRepository = userRepository,
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onRegisterClick = { currentScreen = "register" },
                onLoginSuccess = { user ->
                    currentUser = user
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
                // Pantallas principales CON barras automáticas
                MainLayout(
                    currentUser = currentUser,
                    currentRoute = currentScreen,
                    onNavigate = { 
                        if (it == "login") {
                            currentUser = null
                        }
                        currentScreen = it 
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            "my_reservations" -> MyReservationsScreen(
                                currentUser = currentUser,
                                allReservations = userRepository.getAllReservations()
                            )
                            "new_reservation" -> NewReservationScreen(
                                user = userActual,
                                userRepository = userRepository,
                                onReservationFinished = {
                                    currentScreen = "my_reservations"
                                }
                            )
                            "profile" -> ProfileScreen(
                                user = currentUser,
                                onNavigateToVehicles = { currentScreen = "my_vehicles" }
                            )
                            "my_vehicles" -> MyVehiclesScreen(
                                user = currentUser,
                                userRepository = userRepository
                            )
                        }
                    }
                }
            }
        }
    }
}
