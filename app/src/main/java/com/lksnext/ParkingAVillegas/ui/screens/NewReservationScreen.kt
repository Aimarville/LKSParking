package com.lksnext.ParkingAVillegas.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.lksnext.ParkingAVillegas.data.UserRepository
import com.lksnext.ParkingAVillegas.model.*
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewReservationScreen(
    user: User?,
    userRepository: UserRepository,
    onReservationFinished: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var startTime by remember { mutableStateOf<Calendar?>(null) }
    var endTime by remember { mutableStateOf<Calendar?>(null) }
    var selectedSpot by remember { mutableStateOf<ParkingSpot?>(null) }
    var filterType by remember { mutableStateOf("TODAS") }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nueva Reserva", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Completa los pasos para tu reserva", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        // Stepper
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StepItem("1", "Vehículo", currentStep >= 1, currentStep > 1)
            StepItem("2", "Horario", currentStep >= 2, currentStep > 2)
            StepItem("3", "Plaza", currentStep >= 3, currentStep > 3)
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    when (currentStep) {
                        1 -> StepOne(user, selectedVehicle) { selectedVehicle = it }
                        2 -> StepTwo(selectedDate, { selectedDate = it }, startTime, { startTime = it }, endTime, { endTime = it })
                        3 -> StepThree(userRepository, selectedVehicle, selectedDate, startTime, endTime, selectedSpot, { selectedSpot = it }, filterType, { filterType = it })
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { if (currentStep > 1) currentStep-- },
                        enabled = currentStep > 1,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp)
                    ) { Text("ATRÁS") }

                    Spacer(Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (currentStep < 3) {
                                if (validateStep(currentStep, selectedVehicle, selectedDate, startTime, endTime, context)) currentStep++
                            } else {
                                if (selectedSpot != null && selectedDate != null && startTime != null && endTime != null) {
                                    val finalStart = (startTime!!.clone() as Calendar)
                                    val finalEnd = (endTime!!.clone() as Calendar)
                                    val finalDate = (selectedDate!!.clone() as Calendar)

                                    val newRes = Reservation(
                                        id = UUID.randomUUID().toString(),
                                        userEmail = user?.email ?: "",
                                        vehiclePlate = selectedVehicle!!.plate,
                                        spotId = selectedSpot!!.id,
                                        date = selectedDate!!.timeInMillis,
                                        startTime = startTime!!.timeInMillis,
                                        endTime = endTime!!.timeInMillis
                                    )

                                    if (userRepository.addReservation(newRes)) {
                                        Toast.makeText(context, "¡Reserva confirmada!", Toast.LENGTH_SHORT).show()
                                        onReservationFinished()
                                    } else {
                                        Toast.makeText(context, "La plaza ya no está disponible", Toast.LENGTH_SHORT).show()
                                    }
                                } else if (selectedSpot == null) {
                                    Toast.makeText(context, "Por favor selecciona una plaza", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS)
                    ) { Text(if (currentStep < 3) "SIGUIENTE" else "FINALIZAR") }
                }
            }
        }
    }
}

fun validateStep(
    step: Int,
    vehicle: Vehicle?,
    date: Calendar?,
    start: Calendar?,
    end: Calendar?,
    context: android.content.Context
): Boolean {
    when (step) {
        1 -> {
            if (vehicle == null) {
                Toast.makeText(context, "Por favor selecciona un vehículo", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        2 -> {
            if (date == null || start == null || end == null) {
                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return false
            }

            val now = Calendar.getInstance()
            if (start.before(now)) {
                Toast.makeText(context, "No puedes reservar una hora que ya ha pasado", Toast.LENGTH_SHORT).show()
                return false
            }

            val durationMs = end.timeInMillis - start.timeInMillis
            val hours = durationMs / (1000 * 60 * 60.0)

            if (hours <= 0) {
                Toast.makeText(context, "La hora de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show()
                return false
            }
            if (hours > 9) {
                Toast.makeText(context, "La duración máxima permitida es de 9 horas", Toast.LENGTH_SHORT).show()
                return false
            }
        }
    }
    return true
}

@Composable
fun StepItem(number: String, text: String, isActive: Boolean, isCompleted: Boolean) {
    val color = if (isActive || isCompleted) OrangeLKS else Color.Gray.copy(alpha = 0.5f)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(number, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = if (isActive || isCompleted) Color.Black else Color.Gray,
            fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOne(
    user: User?,
    selectedVehicle: Vehicle?,
    onVehicleSelected: (Vehicle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val vehicles = user?.vehiculos ?: emptyList()

    Column {
        Text("Selecciona tu vehículo", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedVehicle?.let { "${it.plate} - ${it.brand} ${it.model}" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehículo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (vehicles.isEmpty()) {
                    DropdownMenuItem(text = { Text("No tienes vehículos registrados") }, onClick = {})
                } else {
                    vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text("${vehicle.plate} - ${vehicle.brand} ${vehicle.model}") },
                            onClick = {
                                onVehicleSelected(vehicle)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepTwo(
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit,
    startTime: Calendar?,
    onStartTimeSelected: (Calendar) -> Unit,
    endTime: Calendar?,
    onEndTimeSelected: (Calendar) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column {
        Text("Selecciona fecha y hora", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().clickable {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, y, m, d ->
                    val newDate = Calendar.getInstance().apply {
                        set(y, m, d)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateSelected(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
                datePicker.maxDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
            }.show()
        }) {
            OutlinedTextField(
                value = selectedDate?.let { dateFormat.format(it.time) } ?: "",
                onValueChange = {},
                label = { Text("Fecha *") },
                readOnly = true,
                enabled = false,
                trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = OrangeLKS) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray,
                    disabledTrailingIconColor = OrangeLKS
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).clickable {
                if (selectedDate == null) {
                    Toast.makeText(context, "Selecciona primero una fecha", Toast.LENGTH_SHORT).show()
                } else {
                    val base = startTime ?: Calendar.getInstance()
                    TimePickerDialog(context, { _, h, min ->
                        val cal = (selectedDate.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, h)
                            set(Calendar.MINUTE, min)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onStartTimeSelected(cal)
                    }, base.get(Calendar.HOUR_OF_DAY), base.get(Calendar.MINUTE), true).show()
                }
            }) {
                OutlinedTextField(
                    value = startTime?.let { timeFormat.format(it.time) } ?: "08:00",
                    onValueChange = {},
                    label = { Text("Hora de inicio *") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    )
                )
            }
            
            Box(modifier = Modifier.weight(1f).clickable {
                if (selectedDate == null) {
                    Toast.makeText(context, "Selecciona primero una fecha", Toast.LENGTH_SHORT).show()
                } else {
                    val base = endTime ?: Calendar.getInstance()
                    TimePickerDialog(context, { _, h, min ->
                        val cal = (selectedDate.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, h)
                            set(Calendar.MINUTE, min)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onEndTimeSelected(cal)
                    }, base.get(Calendar.HOUR_OF_DAY), base.get(Calendar.MINUTE), true).show()
                }
            }) {
                OutlinedTextField(
                    value = endTime?.let { timeFormat.format(it.time) } ?: "12:00",
                    onValueChange = {},
                    label = { Text("Hora de fin *") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        if (startTime != null && endTime != null) {
            val diff = endTime.timeInMillis - startTime.timeInMillis
            val hours = diff / (1000 * 60 * 60.0)
            if (hours > 0) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF0288D1))
                        Spacer(Modifier.width(8.dp))
                        Text("Duración de la reserva: ", fontSize = 14.sp)
                        Text("${String.format(Locale.getDefault(), "%.1f", hours)}h", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        
        Surface(
            color = Color(0xFFE1F5FE),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF0288D1), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Restricciones:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0288D1))
                }
                Text("• Duración máxima: 9 horas", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 28.dp))
                Text("• Anticipación máxima: 7 días", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 28.dp))
            }
        }
    }
}

@Composable
fun StepThree(
    userRepository: UserRepository,
    selectedVehicle: Vehicle?,
    selectedDate: Calendar?,
    startTime: Calendar?,
    endTime: Calendar?,
    selectedSpot: ParkingSpot?,
    onSpotSelected: (ParkingSpot) -> Unit,
    filterType: String,
    onFilterChange: (String) -> Unit
) {
    val allSpots = remember {
        val list = mutableListOf<ParkingSpot>()
        for (i in 1..15) list.add(ParkingSpot("A-${String.format("%02d", i)}", SpotType.NORMAL))
        for (i in 1..8) list.add(ParkingSpot("E-${String.format("%02d", i)}", SpotType.ELECTRIC))
        for (i in 1..4) list.add(ParkingSpot("D-${String.format("%02d", i)}", SpotType.DISABLED))
        for (i in 1..3) list.add(ParkingSpot("M-${String.format("%02d", i)}", SpotType.MOTORCYCLE))
        list
    }

    val filteredSpots = remember(selectedVehicle, filterType, selectedDate, startTime, endTime, userRepository.reservationsState.size) {
        allSpots.filter { spot ->
            val isCompatible = when (selectedVehicle?.type) {
                VehicleType.MOTORCYCLE -> spot.type == SpotType.MOTORCYCLE
                else -> when (spot.type) {
                    SpotType.NORMAL -> true
                    SpotType.ELECTRIC -> selectedVehicle?.isElectric == true
                    SpotType.DISABLED -> selectedVehicle?.isDisabled == true
                    else -> false
                }
            }
            val isAvailable = if(selectedDate != null && startTime != null && endTime != null) {
                userRepository.isSpotAvailable(
                    spot.id,
                    selectedDate.timeInMillis,
                    startTime.timeInMillis,
                    endTime.timeInMillis
                )
            } else true
            val matchesFilter = if (filterType == "TODAS") true else spot.type.name == filterType

            isCompatible && matchesFilter && isAvailable
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Selecciona una plaza disponible", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Text("Filtrar por tipo:", fontSize = 14.sp, color = Color.Gray)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton("TODAS", filterType == "TODAS") { onFilterChange("TODAS") }
            if (selectedVehicle?.type == VehicleType.MOTORCYCLE) {
                FilterButton("MOTORCYCLE", filterType == "MOTORCYCLE") { onFilterChange("MOTORCYCLE") }
            } else {
                FilterButton("NORMAL", filterType == "NORMAL") { onFilterChange("NORMAL") }
                if (selectedVehicle?.isElectric == true) FilterButton("ELECTRIC", filterType == "ELECTRIC") { onFilterChange("ELECTRIC") }
                if (selectedVehicle?.isDisabled == true) FilterButton("DISABLED", filterType == "DISABLED") { onFilterChange("DISABLED") }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredSpots) { spot ->
                val isSelected = selectedSpot?.id == spot.id
                val icon = when (spot.type) {
                    SpotType.NORMAL -> Icons.Default.DirectionsCar
                    SpotType.ELECTRIC -> Icons.Default.EvStation
                    SpotType.DISABLED -> Icons.Default.Accessible
                    SpotType.MOTORCYCLE -> Icons.Default.TwoWheeler
                }

                Surface(
                    modifier = Modifier
                        .clickable { onSpotSelected(spot) }
                        .border(1.dp, if (isSelected) OrangeLKS else Color.LightGray, RoundedCornerShape(8.dp)),
                    color = if (isSelected) OrangeLKS.copy(alpha = 0.1f) else Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = if (isSelected) OrangeLKS else Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(spot.id)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        color = if (isSelected) Color(0xFFE0E0E0) else Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = if (label == "NORMAL") "P NORMAL" else label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.Gray
            )
        }
    }
}
