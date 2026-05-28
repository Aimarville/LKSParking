package com.lksnext.ParkingAVillegas.ui.components.reservation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.state.ReservationUiState
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun StepThree(
    uiState: ReservationUiState,
    onSpotSelected: (ParkingSpot) -> Unit,
    onFilterChange: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Selecciona una plaza disponible",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Filtrar por tipo:",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterButton(
                label = "TODAS",
                isSelected = uiState.filterType == "TODAS"
            ) {
                onFilterChange("TODAS")
            }

            if (uiState.selectedVehicle?.type == VehicleType.MOTORCYCLE) {

                FilterButton(
                    label = "MOTORCYCLE",
                    isSelected = uiState.filterType == "MOTORCYCLE"
                ) {
                    onFilterChange("MOTORCYCLE")
                }

            } else {

                FilterButton(
                    label = "NORMAL",
                    isSelected = uiState.filterType == "NORMAL"
                ) {
                    onFilterChange("NORMAL")
                }

                if (uiState.selectedVehicle?.isElectric == true) {

                    FilterButton(
                        label = "ELECTRIC",
                        isSelected = uiState.filterType == "ELECTRIC"
                    ) {
                        onFilterChange("ELECTRIC")
                    }
                }

                if (uiState.selectedVehicle?.isDisabled == true) {

                    FilterButton(
                        label = "DISABLED",
                        isSelected = uiState.filterType == "DISABLED"
                    ) {
                        onFilterChange("DISABLED")
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(uiState.availableSpots) { spot ->

                val isSelected =
                    uiState.selectedSpot?.id == spot.id

                val icon = when (spot.type) {

                    SpotType.NORMAL ->
                        Icons.Default.DirectionsCar

                    SpotType.ELECTRIC ->
                        Icons.Default.EvStation

                    SpotType.DISABLED ->
                        Icons.Default.Accessible

                    SpotType.MOTORCYCLE ->
                        Icons.Default.TwoWheeler
                }

                Surface(
                    modifier = Modifier
                        .clickable {
                            onSpotSelected(spot)
                        }
                        .border(
                            width = 1.dp,
                            color =
                                if (isSelected) OrangeLKS
                                else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    color =
                        if (isSelected)
                            OrangeLKS.copy(alpha = 0.1f)
                        else Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint =
                                if (isSelected) OrangeLKS
                                else Color.Gray
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = spot.id)
                    }
                }
            }
        }
    }
}