package com.lksnext.ParkingAVillegas.ui.components.reservation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.ui.state.ReservationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOne(
    uiState: ReservationUiState,
    onVehicleSelected: (Vehicle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val vehicles = uiState.user?.vehiculos ?: emptyList()

    Column {
        Text(
            "Selecciona tu vehículo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            OutlinedTextField(
                value = uiState.selectedVehicle?.let {
                    "${it.plate} - ${it.brand} ${it.model}"
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Vehículo")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                if (vehicles.isEmpty()) {

                    DropdownMenuItem(
                        text = {
                            Text("No tienes vehículos registrados")
                        },
                        onClick = {}
                    )

                } else {

                    vehicles.forEach { vehicle ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehicle.plate} - ${vehicle.brand} ${vehicle.model}"
                                )
                            },
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