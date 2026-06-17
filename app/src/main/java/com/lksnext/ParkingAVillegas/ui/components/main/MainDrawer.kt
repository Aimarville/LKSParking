package com.lksnext.ParkingAVillegas.ui.components.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lksnext.ParkingAVillegas.ui.state.MainUiState

@Composable
fun MainDrawer(
    uiState: MainUiState,
    onNavigate: (String) -> Unit
) {

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {

        DrawerHeader(
            user = uiState.currentUser
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerItem(
            label = "Mis Reservas",
            icon = Icons.Default.DateRange,
            isSelected =
                uiState.currentRoute == "my_reservations"
        ) {
            onNavigate("my_reservations")
        }

        DrawerItem(
            label = "Nueva Reserva",
            icon = Icons.Default.AssignmentTurnedIn,
            isSelected =
                uiState.currentRoute == "new_reservation"
        ) {
            onNavigate("new_reservation")
        }

        DrawerItem(
            label = "Perfil",
            icon = Icons.Default.Person,
            isSelected =
                uiState.currentRoute == "profile"
        ) {
            onNavigate("profile")
        }

        HorizontalDivider()

        DrawerItem(
            label = "Cerrar Sesión",
            icon = Icons.AutoMirrored.Filled.Logout,
            isSelected = false
        ) {
            onNavigate("login")
        }
    }
}