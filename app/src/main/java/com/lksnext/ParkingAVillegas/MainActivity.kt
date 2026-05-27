package com.lksnext.ParkingAVillegas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.navigation.AppNavigation
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

