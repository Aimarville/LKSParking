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
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.ui.components.profile.*

@Composable
fun ProfileScreen(
    userEmail: String, // Pasamos el email en lugar del objeto User entero
    userRepository: UserRepository,
    onNavigateToVehicles: () -> Unit
) {
    // 1. OBTENER ESTADO ACTUALIZADO
    // Buscamos al usuario en la lista observable del repo
    val user = userRepository.usersState.find { it.email == userEmail }
    val context = LocalContext.current

    // 2. ESTADOS PARA EDICIÓN
    var isEditing by remember { mutableStateOf(false) }
    var editNombre by remember { mutableStateOf(user?.nombre ?: "") }
    var editTelefono by remember { mutableStateOf(user?.telefono ?: "") }
    var editDept by remember { mutableStateOf(user?.departamento ?: "") }
    var photoUri by remember { mutableStateOf<Uri?>(user?.profilePhotoUri?.let { Uri.parse(it) }) }

    // 3. LAUNCHER PARA LA CÁMARA/GALERÍA
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
        // Opcional: Guardar inmediatamente en el repo
        userRepository.updateProfile(userEmail, editNombre, editTelefono, editDept, uri?.toString())
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
                    // --- AVATAR FUNCIONAL ---
                    ProfileAvatar(
                        userName = user?.nombre ?: "",
                        photoUri = photoUri,
                        onClick = {
                            launcher.launch("image/*")
                        }
                    )

                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(user?.nombre ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                // --- CAMPOS EDITABLES ---
                ProfileField(label = "Nombre Completo *", value = editNombre, isReadOnly = !isEditing) { editNombre = it }
                ProfileField(label = "Teléfono *", value = editTelefono, isReadOnly = !isEditing) { editTelefono = it }
                ProfileField(label = "Departamento *", value = editDept, isReadOnly = !isEditing) { editDept = it }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            // GUARDAR CAMBIOS
                            userRepository.updateProfile(userEmail, editNombre, editTelefono, editDept, photoUri?.toString())
                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = !isEditing
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color.Black else OrangeLKS),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "GUARDAR CAMBIOS" else "EDITAR PERFIL")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CARD MIS VEHICULOS ---
        VehiclesSummaryCard(
            vehicleCount = user?.vehiculos?.size ?: 0,
            onClick = onNavigateToVehicles
        )
    }
}