package com.example.lksparking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.model.Reservation
import com.example.lksparking.model.User
import com.example.lksparking.ui.theme.BlueInfoBg
import com.example.lksparking.ui.theme.OrangeLKS
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    currentUser: User?,
    allReservations: List<Reservation>
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var userReservations = allReservations.filter { it.userEmail == currentUser?.email }

    val today = Calendar.getInstance()

    val vigentes = userReservations.filter { it.date.after(today) || isSameDay(it.date, today) }
    val historicas = userReservations.filter { it.date.before(today) && !isSameDay(it.date, today) }

    var tabs = listOf("VIGENTES (${vigentes.size})", "HISTORICAS (${historicas.size})")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Titulo de la seccion
        Text(
            text = "Mis Reservas",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Pestañas (Tabs)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OrangeLKS
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {selectedTab = index},
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        val currentList = if (selectedTab == 0) vigentes else historicas

        if (currentList.isEmpty()) {
            Box(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(BlueInfoBg, RoundedCornerShape(8.dp)).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF0288D1))
                    Spacer(Modifier.width(12.dp))
                    Text(if (selectedTab == 0) "No tienes reservas vigentes." else "No hay historial.")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList) { reservation ->
                    ReservationCard(reservation)
                }
            }
        }
    }
}

@Composable
fun ReservationCard(res: Reservation) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icono de Parking
            Surface(
                color = OrangeLKS.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalParking, null, tint = OrangeLKS, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Plaza ${res.spotId}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(dateFormat.format(res.date.time), color = Color.Gray, fontSize = 14.sp)
                Text(
                    "${timeFormat.format(res.startTime.time)} - ${timeFormat.format(res.endTime.time)}",
                    color = OrangeLKS,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(res.vehiclePlate, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                // Badge de estado
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Confirmada", color = Color(0xFF2E7D32), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}