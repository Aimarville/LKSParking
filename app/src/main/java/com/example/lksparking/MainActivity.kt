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
import com.example.lksparking.ui.screens.ForgotPasswordScreen
import com.example.lksparking.ui.screens.LoginScreen
import com.example.lksparking.ui.screens.MainLayout
import com.example.lksparking.ui.screens.MyReservationsScreen
import com.example.lksparking.ui.screens.NewReservationScreen
import com.example.lksparking.ui.screens.ProfileScreen
import com.example.lksparking.ui.screens.RegisterScreen
import com.example.lksparking.ui.theme.LKSParkingTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.lksparking.ui.screens.MyVehiclesScreen

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

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            "login" -> LoginScreen(
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onRegisterClick = { currentScreen = "register" },
                onLoginAction = { currentScreen = "my_reservations" }
            )

            "forgot_password" -> ForgotPasswordScreen(
                onBackClick = { currentScreen = "login" }
            )

            "register" -> RegisterScreen(
                onLoginClick = { currentScreen = "login" }
            )

            else -> {
                // Pantallas principales CON barras automaticas
                MainLayout(
                    currentRoute = currentScreen,
                    onNavigate = { currentScreen = it }) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            "my_reservations" -> MyReservationsScreen()
                            "new_reservation" -> NewReservationScreen()
                            "profile" -> ProfileScreen(onNavigateToVehicles = {currentScreen = "my_vehicles"})
                            "my_vehicles" -> MyVehiclesScreen()
                        }
                    }
                }
            }
        }
    }
}
