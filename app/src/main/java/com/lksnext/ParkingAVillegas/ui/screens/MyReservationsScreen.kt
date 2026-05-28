package com.lksnext.ParkingAVillegas.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.data.static.ParkingData
import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.components.reservation.EditReservationDialog
import com.lksnext.ParkingAVillegas.ui.components.reservation.EmptyReservations
import com.lksnext.ParkingAVillegas.ui.components.reservation.ReservationCard
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.validation.ReservationValidator
import com.lksnext.ParkingAVillegas.validation.VehicleValidator
import com.lksnext.ParkingAVillegas.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    viewModel: ReservationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var reservationToEdit by remember {
        mutableStateOf<Reservation?>(null)
    }

    val context = LocalContext.current

    val tabs = listOf(
        "VIGENTES (${uiState.currentReservations.size})",
        "HISTÓRICAS (${uiState.historicalReservations.size})"
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        Text("Mis Reservas", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        val currentList = if (selectedTab == 0)
            uiState.currentReservations
        else
            uiState.historicalReservations

        if (currentList.isEmpty()) {
            EmptyReservations(isHistory = selectedTab == 1)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList) { reservation ->
                    ReservationCard(
                        res = reservation,
                        isHistory = selectedTab == 1,
                        onDelete = {
                            viewModel.deleteReservation(reservation.id)
                        },
                        onEdit = { reservationToEdit = reservation }
                    )
                }
            }
        }
    }

    reservationToEdit?.let { reservation ->
        EditReservationDialog(
            res = reservation,
            user = uiState.user,
            onDismiss = {
                reservationToEdit = null
            },
            onConfirm = { updatedReservation ->

                val success =
                    viewModel.updateReservation(updatedReservation)

                if (success) {

                    Toast.makeText(
                        context,
                        "Reserva actualizada",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Cierra dialog
                    reservationToEdit = null

                } else {

                    Toast.makeText(
                        context,
                        "La plaza ya no está disponible",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}
