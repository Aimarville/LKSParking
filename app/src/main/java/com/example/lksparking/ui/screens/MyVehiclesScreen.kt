package com.example.lksparking.ui.screens

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.ui.theme.OrangeLKS

@Composable
fun MyVehiclesScreen() {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Mis Vehículos", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("Gestiona los vehículos registrados en tu cuenta", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = {showAddDialog = true},
                colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Text("AÑADIR")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Tarjeta de ejemplo del vehículo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, null, tint = OrangeLKS, modifier = Modifier.size(48.dp))
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("1234ABC", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Toyota corolla", color = Color.Gray)
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        SuggestionChip(onClick = {}, label = {Text("Blanco")})
                        Spacer(Modifier.width(8.dp))
                        SuggestionChip(onClick = {}, label = {Text("Automóvil")})
                    }
                }
                IconButton(onClick = {}) {Icon(Icons.Default.Edit, null, tint = OrangeLKS)}
                IconButton(onClick = {}) {Icon(Icons.Default.Delete, null, tint = Color.Red)}
            }
        }
    }

    // Lamada al diálogo
    if (showAddDialog) {
        AddVehicleDialog(onDismiss = {showAddDialog = false})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(onDismiss: () -> Unit) {
    // Estados para el Dropdown
    var expanded by remember { mutableStateOf(false) }
    var options = listOf("Automóvil", "Motocicleta")
    var selectedType by remember { mutableStateOf(options[0]) }

    // Estados para los Checkboxes
    var isMinusvalido by remember { mutableStateOf(false) }
    var isElectrico by remember { mutableStateOf(false) }

    // Logica: Si es Motocicleta, desactivamos y desmarcamos los checkboxes
    var areCheckboxesEnabled = selectedType == "Automóvil"

    // Si cambia a Motocicleta, reseteamos los valores
    LaunchedEffect(selectedType) {
        if (selectedType == "Motocicleta") {
            isMinusvalido = false
            isElectrico = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                shape = RoundedCornerShape(4.dp)
            ) { Text("AÑADIR") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {Text("CANCELAR", color = OrangeLKS)}
        },
        title = {Text("Añadir Vehículo", fontWeight = FontWeight.Bold)},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, label = {Text("Matrícula *")}, placeholder = {Text("1234ABC")}, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = {Text("Marca *")}, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = {Text("Modelo *")}, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = {Text("Color *")}, modifier = Modifier.fillMaxWidth())

                // DROPDOWN DE TIPO
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {expanded = !expanded}
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = {Text("Tipo *")},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false}
                    ) {
                        options.forEach {selectionOption ->
                            DropdownMenuItem(
                                text = {Text(selectionOption)},
                                onClick = {
                                    selectedType = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // CHECKBOXES CON LOGICA
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isMinusvalido,
                            onCheckedChange = {isMinusvalido = it},
                            enabled = areCheckboxesEnabled
                        )
                        Text(
                            "Vehículo de Minusválido",
                            color = if (areCheckboxesEnabled) Color.Black else Color.Gray
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isElectrico,
                            onCheckedChange = {isElectrico = it},
                            enabled = areCheckboxesEnabled
                        )
                        Text(
                            "Vehículo Eléctrico",
                            color = if (areCheckboxesEnabled) Color.Black else Color.Gray
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    )
}