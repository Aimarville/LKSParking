package com.lksnext.ParkingAVillegas.ui.components.vehicle

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.validation.VehicleValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onVehicleAdded: (Vehicle) -> Unit
) {
    val context = LocalContext.current

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

                    val validation = VehicleValidator.validateVehicle(
                        plate = plate,
                        brand = brand,
                        model = model
                    )

                    if (!validation.isValid) {

                        Toast.makeText(
                            context,
                            validation.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    onVehicleAdded(
                        Vehicle(
                            plate = plate,
                            brand = brand,
                            model = model,
                            type =
                                if (selectedTypeStr == "Motocicleta")
                                    VehicleType.MOTORCYCLE
                                else
                                    VehicleType.AUTOMOBILE,

                            isElectric =
                                if (areCheckboxesEnabled)
                                    isElectric
                                else
                                    false,

                            isDisabled =
                                if (areCheckboxesEnabled)
                                    isDisabled
                                else
                                    false
                        )
                    )
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeLKS
                ),

                shape = RoundedCornerShape(4.dp)

            ) {

                Text("AÑADIR")
            }
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