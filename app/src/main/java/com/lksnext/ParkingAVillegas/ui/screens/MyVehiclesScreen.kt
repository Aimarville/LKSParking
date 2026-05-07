package com.lksnext.ParkingAVillegas.ui.screens

import android.widget.Toast
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
import com.lksnext.ParkingAVillegas.data.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun MyVehiclesScreen(
    user: User?,
    userRepository: UserRepository
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentUserVehicles = user?.email?.let { email ->
        userRepository.getUserByEmail(email)?.vehiculos
    } ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Mis Vehículos", 
                    fontSize = 28.sp, // Ligeramente más pequeño para que sea más adaptable
                    fontWeight = FontWeight.Bold,
                    softWrap = true
                )
                Text(
                    "Gestiona los vehículos registrados en tu cuenta", 
                    fontSize = 12.sp, 
                    color = Color.Gray,
                    softWrap = true
                )
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("AÑADIR", maxLines = 1)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (currentUserVehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes vehículos registrados", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(currentUserVehicles) { vehicle ->
                    VehicleCard(vehicle = vehicle, onDelete = {
                        // REPARACIÓN ELIMINAR:
                        val success = userRepository.removeVehicleFromUser(user?.email ?: "", vehicle.plate)
                        if (success) {
                            Toast.makeText(context, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    if (showAddDialog) {
        AddVehicleDialog(
            onDismiss = { showAddDialog = false },
            onVehicleAdded = { newVehicle ->
                if (userRepository.isPlateRegisteredGlobally(newVehicle.plate)) {
                    Toast.makeText(context, "Este vehículo ya está registrado", Toast.LENGTH_LONG).show()
                } else {
                    // REPARACIÓN AÑADIR:
                    val success = userRepository.addVehicleToUser(user?.email ?: "", newVehicle)
                    if (success) {
                        showAddDialog = false
                        Toast.makeText(context, "Vehículo añadido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsCar, null, tint = OrangeLKS, modifier = Modifier.size(48.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.plate, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("${vehicle.brand} ${vehicle.model}", color = Color.Gray)

                Spacer(Modifier.height(8.dp))

                // Alternativa a FlowRow: Usamos un Row con Scroll o simplemente
                // ponemos los chips en una sola línea que se corta
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val typeLabel = if (vehicle.type == VehicleType.MOTORCYCLE) "Moto" else "Coche"
                    VehicleChip(typeLabel, Color(0xFFE3F2FD), Color(0xFF1976D2))

                    if (vehicle.isElectric) VehicleChip("Eléctrico", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    if (vehicle.isDisabled) VehicleChip("Minusválido", Color(0xFFFFF3E0), Color(0xFFE65100))
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color.Red)
            }
        }
    }
}

@Composable
fun VehicleChip(label: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onVehicleAdded: (Vehicle) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Automóvil", "Motocicleta")
    var selectedTypeStr by remember { mutableStateOf(options[0]) }

    var isElectric by remember { mutableStateOf(false) }
    var isDisabled by remember { mutableStateOf(false) }

    val areCheckboxesEnabled = selectedTypeStr == "Automóvil"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { 
                    if (plate.isNotBlank()) {
                        onVehicleAdded(Vehicle(
                            plate = plate,
                            brand = brand,
                            model = model,
                            type = if (selectedTypeStr == "Motocicleta") VehicleType.MOTORCYCLE else VehicleType.AUTOMOBILE,
                            isElectric = if (areCheckboxesEnabled) isElectric else false,
                            isDisabled = if (areCheckboxesEnabled) isDisabled else false
                        ))
                    } else {
                        // Podríamos añadir un Toast o error visual aquí
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                shape = RoundedCornerShape(4.dp)
            ) { Text("AÑADIR") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = OrangeLKS) }
        },
        title = { Text("Añadir Vehículo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("Matrícula *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Modelo") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedTypeStr,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTypeStr = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (areCheckboxesEnabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isDisabled, onCheckedChange = { isDisabled = it })
                        Text("Vehículo de Minusválido", fontSize = 14.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isElectric, onCheckedChange = { isElectric = it })
                        Text("Vehículo Eléctrico", fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    )
}
