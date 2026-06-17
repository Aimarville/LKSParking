package com.lksnext.ParkingAVillegas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.lksnext.ParkingAVillegas.ui.components.profile.*
import com.lksnext.ParkingAVillegas.viewmodel.ProfileViewModel
import kotlin.contracts.contract

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToVehicles: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val context = LocalContext.current

    // FOTO
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        viewModel.updatePhoto(uri)
    }

    // SUCCESS
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(
                context,
                "Perfil actualizado",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(16.dp)) {
        Text("Mi Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    ProfileAvatar(
                        userName = uiState.name,
                        photoUri = uiState.photoUri,
                        onClick = {
                            launcher.launch("image/*")
                        }
                    )

                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = uiState.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.email,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                // --- CAMPOS EDITABLES ---
                ProfileField(
                    label = "Nombre Completo *",
                    value = uiState.name,
                    isReadOnly = !uiState.isEditing,
                    onValueChange = {
                        viewModel.updateName(it)
                    }
                )

                ProfileField(
                    label = "Teléfono *",
                    value = uiState.phone,
                    isReadOnly = !uiState.isEditing,
                    onValueChange = {
                        viewModel.updatePhone(it)
                    }
                )

                ProfileField(
                    label = "Departamento *",
                    value = uiState.department,
                    isReadOnly = !uiState.isEditing,
                    onValueChange = {
                        viewModel.updateDepartment(it)
                    }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (uiState.isEditing) {
                            viewModel.saveProfile()
                        } else {
                            viewModel.toggleEditing()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.isEditing) Color.Black else OrangeLKS),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState.isEditing)
                            "GUARDAR CAMBIOS"
                        else
                            "EDITAR PERFIL")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CARD MIS VEHICULOS ---
        VehiclesSummaryCard(
            vehicleCount = uiState.vehicleCount,
            onClick = onNavigateToVehicles
        )
    }
}