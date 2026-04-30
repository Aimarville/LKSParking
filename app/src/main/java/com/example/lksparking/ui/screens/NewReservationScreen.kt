package com.example.lksparking.ui.screens

import android.R
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.ui.theme.LKSParkingTheme
import com.example.lksparking.ui.theme.OrangeLKS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReservationScreen() {
    var currentStep by remember {mutableIntStateOf(1)}
    var selectedVehicle by remember {mutableStateOf("")}
    var isExpended by remember {mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nueva Reserva", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(
            "Completa los siguientes pasos para realizar tu reserva",
            fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        // --- STEPPER (Pasos 1, 2, 3) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepItem(number = "1", text = "Seleccionar\nVehiculo", isActive = currentStep == 1)
            StepItem(number = "2", text = "Fecha y\nHora", isActive = currentStep == 2)
            StepItem(number = "3", text = "Seleccionar\nPlaza", isActive = currentStep == 3)
        }

        Spacer(Modifier.height(24.dp))

        // --- CARD PASO 1 ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // --- AQUI CAMBIAMOS EL CONTENIDO SEGUN EL PASO ---
                when (currentStep) {
                    1 -> StepOneContent(
                        selectedVehicle = selectedVehicle,
                        isExpanded = isExpended,
                        onExpandedChange = {isExpended = it},
                        onVehicleSelect = {selectedVehicle = it; isExpended = false}
                    )
                    2 -> StepTwoContent()
                    3 -> Text("Contenido del Paso 3")
                }

                Spacer(Modifier.height(32.dp))

                // Botones de navegacion
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(
                        onClick = {if (currentStep > 1) currentStep--},
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text("ATRÁS", color = Color.LightGray)
                    }

                    Button(
                        onClick = {if (currentStep < 3) currentStep++},
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("SIGUIENTE")
                    }
                }
            }
        }
    }
}

@Composable
fun StepItem(number: String, text: String, isActive: Boolean) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = if (isActive) Color.Black else Color.Gray,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// --- CONTENIDO PASO 1 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOneContent(
    selectedVehicle: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onVehicleSelect: (String) -> Unit
) {
    Column {
        Text("Selecciona tu vehículo", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = selectedVehicle,
                onValueChange = {},
                readOnly = true,
                label = {Text("Vehículo")},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)},
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {onExpandedChange(false)}
            ) {
                DropdownMenuItem(
                    text = {Text("Coche - 1234ABC")},
                    onClick = {onVehicleSelect("Coche - 1234ABC")}
                )
            }
        }
    }
}

// --- CONTENIDO PASO 2 ---
@Composable
fun StepTwoContent() {
    Column {
        Text("Selecciona fecha y hora", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = {Text("Fecha *")},
            readOnly = true,
            trailingIcon = {Icon(Icons.Default.DateRange, null)},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = "08:00",
                onValueChange = {},
                label = {Text("Hora de inicio *")},
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = "12:00",
                onValueChange = {},
                label = {Text("Hora de fin *")},
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // El aviso azul de duracion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null, tint = Color(0xFF0288D1))
            Spacer(Modifier.width(8.dp))
            Text("Duración de la reserva: ", fontSize = 14.sp)
            Text("4h", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewReservationScreenPreview() {
    LKSParkingTheme {
        NewReservationScreen()
    }
}