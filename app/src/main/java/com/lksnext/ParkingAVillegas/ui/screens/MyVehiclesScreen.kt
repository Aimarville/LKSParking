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
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.ui.components.vehicle.AddVehicleDialog
import com.lksnext.ParkingAVillegas.ui.components.vehicle.VehicleCard
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.viewmodel.AuthViewModel
import com.lksnext.ParkingAVillegas.viewmodel.VehicleViewModel

@Composable
fun MyVehiclesScreen(
    viewModel: VehicleViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // Escuchar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearError()
        }
    }

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

        if (uiState.vehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes vehículos registrados", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.vehicles) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onDelete = {
                            viewModel.deleteVehicle(vehicle.plate)

                            Toast.makeText(
                                context,
                                "Vehículo eliminado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddVehicleDialog(
            onDismiss = { showAddDialog = false },
            onVehicleAdded = { newVehicle ->
                viewModel.addVehicle(newVehicle)

                showAddDialog = false

                Toast.makeText(
                    context,
                    "Vehículo añadido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}