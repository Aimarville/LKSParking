package com.lksnext.ParkingAVillegas.ui.components.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MainBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    NavigationBar(
        containerColor = Color.White
    ) {

        NavigationBarItem(
            selected = currentRoute == "my_reservations",
            onClick = {
                onNavigate("my_reservations")
            },
            label = {
                Text("Mis Reservas")
            },
            icon = {
                Icon(Icons.Default.DateRange, null)
            }
        )

        NavigationBarItem(
            selected = currentRoute == "new_reservation",
            onClick = {
                onNavigate("new_reservation")
            },
            label = {
                Text("Nueva Reserva")
            },
            icon = {
                Icon(Icons.Default.AssignmentTurnedIn, null)
            }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                onNavigate("profile")
            },
            label = {
                Text("Perfil")
            },
            icon = {
                Icon(Icons.Default.Person, null)
            }
        )
    }
}