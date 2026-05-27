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
import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.validation.ReservationValidator
import com.lksnext.ParkingAVillegas.validation.VehicleValidator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    currentUser: User?,
    allReservations: List<Reservation>,
    allParkingSpots: List<ParkingSpot>,
    userRepository: UserRepository
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var reservationToEdit by remember { mutableStateOf<Reservation?>(null) }
    val context = LocalContext.current

    val userReservations = allReservations.filter { it.userEmail == currentUser?.email }
    val now = System.currentTimeMillis() // Usamos Long para comparar con Long

    val vigentes = userReservations.filter { res ->
        res.endTime >= now // Comparación directa de Long
    }.sortedBy { it.startTime }

    val historicas = userReservations.filter { res ->
        res.endTime < now
    }.sortedByDescending { it.startTime }

    val tabs = listOf("VIGENTES (${vigentes.size})", "HISTÓRICAS (${historicas.size})")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
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

        val currentList = if (selectedTab == 0) vigentes else historicas

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
                        onDelete = { userRepository.deleteReservation(reservation.id) },
                        onEdit = { reservationToEdit = reservation }
                    )
                }
            }
        }
    }

    if (reservationToEdit != null) {
        EditReservationDialog(
            res = reservationToEdit!!,
            user = currentUser,
            parkingSpots = allParkingSpots,
            onDismiss = { reservationToEdit = null },
            onConfirm = { updatedRes ->
                if (userRepository.updateReservation(updatedRes)) {
                    Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                    reservationToEdit = null
                } else {
                    Toast.makeText(context, "Plaza no disponible en ese horario", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
fun EmptyReservations(isHistory: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isHistory) Icons.Default.History else Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isHistory) "No tienes reservas pasadas" else "No tienes reservas activas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
fun ReservationCard(
    res: Reservation,
    isHistory: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (isHistory) Color.Gray.copy(0.1f) else OrangeLKS.copy(0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalParking, null, tint = if (isHistory) Color.Gray else OrangeLKS)
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Plaza ${res.spotId}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Text(
                        text = dateFormat.format(Date(res.date)),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "${timeFormat.format(Date(res.startTime))} - ${timeFormat.format(Date(res.endTime))}",
                        color = if (isHistory) Color.Gray else OrangeLKS,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(res.vehiclePlate, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (isHistory) Color(0xFFEEEEEE) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isHistory) "Completada" else "Confirmada",
                            color = if (isHistory) Color.Gray else Color(0xFF2E7D32),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (!isHistory) {
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red)
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }
                }
            }
        }
    }
}

@Composable
fun EditReservationDialog(
    res: Reservation,
    user: User?,
    parkingSpots: List<ParkingSpot>,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {
    var selectedVehiclePlate by remember { mutableStateOf(res.vehiclePlate) }

    // Convertimos el Long del modelo a un objeto Calendar para que la UI pueda manejarlo
    var startTime by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = res.startTime })
    }
    var endTime by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = res.endTime })
    }

    val context = LocalContext.current
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val currentSpot = parkingSpots.find {it.id == res.spotId}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Reserva", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Vehículo:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                user?.vehiculos?.forEach { v ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedVehiclePlate = v.plate }
                    ) {
                        RadioButton(selected = selectedVehiclePlate == v.plate, onClick = { selectedVehiclePlate = v.plate })
                        Text("${v.brand} - ${v.plate}")
                    }
                }

                HorizontalDivider()

                Text("Horario:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Selector de Hora de Inicio
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val now = Calendar.getInstance()

                                    val newStart = (startTime.clone() as Calendar).apply {
                                        set(Calendar.HOUR_OF_DAY, h)
                                        set(Calendar.MINUTE, m)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }

                                    // No permitir hora anterior a la actual
                                    if (newStart.before(now)) {
                                        Toast.makeText(
                                            context,
                                            "La hora de inicio no puede ser anterior a la actual",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@TimePickerDialog
                                    }

                                    // Si el end actual supera las 9h, ajustarlo automaticamente
                                    val maxEnd = (newStart.clone() as Calendar).apply {
                                        add(Calendar.HOUR_OF_DAY, 9)
                                    }

                                    val timeValidation =
                                        ReservationValidator.validateReservationTime(
                                            startTime,
                                            endTime
                                        )

                                    if (!timeValidation.isValid) {

                                        Toast.makeText(
                                            context,
                                            timeValidation.errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@TimePickerDialog
                                    }

                                    startTime = newStart
                                },
                                startTime.get(Calendar.HOUR_OF_DAY),
                                startTime.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("In: ${timeFormat.format(startTime.time)}")
                    }

                    // Selector de Hora de Fin
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->

                                    val newEnd = (endTime.clone() as Calendar).apply {
                                        set(Calendar.HOUR_OF_DAY, h)
                                        set(Calendar.MINUTE, m)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }

                                    val maxEnd = (startTime.clone() as Calendar).apply {
                                        add(Calendar.HOUR_OF_DAY, 9)
                                    }

                                    when {

                                        // Debe ser posterior al inicio
                                        !newEnd.after(startTime) -> {
                                            Toast.makeText(
                                                context,
                                                "La salida debe ser después de la entrada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        // Máximo 9 horas
                                        newEnd.after(maxEnd) -> {
                                            Toast.makeText(
                                                context,
                                                "La reserva no puede superar las 9 horas",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        else -> {
                                            endTime = newEnd
                                        }
                                    }

                                },
                                endTime.get(Calendar.HOUR_OF_DAY),
                                endTime.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Out: ${timeFormat.format(endTime.time)}")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (endTime.after(startTime)) {
                    val selectedVehicle = user
                        ?.vehiculos
                        ?.find {it.plate == selectedVehiclePlate}

                    if (selectedVehicle == null || currentSpot == null) {
                        Toast.makeText(
                            context,
                            "Error validando vehículo",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val vehicleValidation =
                        VehicleValidator.validateVehicleForSpot(
                            selectedVehicle,
                            currentSpot.type
                        )

                    if (!vehicleValidation.isValid) {

                        Toast.makeText(
                            context,
                            vehicleValidation.errorMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    // Pasamos .timeInMillis porque el modelo espera un Long
                    onConfirm(res.copy(
                        vehiclePlate = selectedVehiclePlate,
                        startTime = startTime.timeInMillis, // Convertido a Long
                        endTime = endTime.timeInMillis,     // Convertido a Long
                        date = res.date                     // res.date ya es Long
                    ))
                } else {
                    Toast.makeText(context, "La salida debe ser después de la entrada", Toast.LENGTH_SHORT).show()
                }
            }) { Text("GUARDAR") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}
