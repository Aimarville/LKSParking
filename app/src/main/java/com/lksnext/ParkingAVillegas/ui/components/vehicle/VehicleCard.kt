package com.lksnext.ParkingAVillegas.ui.components.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

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