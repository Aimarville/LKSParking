package com.lksnext.ParkingAVillegas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lksnext.ParkingAVillegas.data.repository.reservation.ReservationRepository
import com.lksnext.ParkingAVillegas.data.repository.reservation.ReservationRepositoryImpl
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepositoryImpl
import com.lksnext.ParkingAVillegas.data.repository.vehicle.VehicleRepository
import com.lksnext.ParkingAVillegas.data.repository.vehicle.VehicleRepositoryImpl
import com.lksnext.ParkingAVillegas.navigation.AppNavigation
import com.lksnext.ParkingAVillegas.ui.theme.LKSParkingTheme

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var reservationRepository: ReservationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepositoryImpl()
        vehicleRepository = VehicleRepositoryImpl(userRepository)
        reservationRepository = ReservationRepositoryImpl()

        enableEdgeToEdge()
        setContent {
            LKSParkingTheme {
                AppNavigation(
                    userRepository = userRepository,
                    vehicleRepository = vehicleRepository,
                    reservationRepository = reservationRepository
                )
            }
        }
    }
}
