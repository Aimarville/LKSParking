package com.lksnext.ParkingAVillegas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.*
import com.lksnext.ParkingAVillegas.ui.components.reservation.StepItem
import com.lksnext.ParkingAVillegas.ui.components.reservation.StepOne
import com.lksnext.ParkingAVillegas.ui.components.reservation.StepThree
import com.lksnext.ParkingAVillegas.ui.components.reservation.StepTwo
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.viewmodel.ReservationViewModel
import java.util.*

@Composable
fun NewReservationScreen(
    viewModel: ReservationViewModel,
    onReservationFinished: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    // SUCCESS
    LaunchedEffect(uiState.success) {

        if (uiState.success) {

            Toast.makeText(
                context,
                "¡Reserva confirmada!",
                Toast.LENGTH_SHORT
            ).show()

            onReservationFinished()

            viewModel.clearSuccess()
        }
    }

    // ERROR
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Nueva Reserva",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Completa los pasos para tu reserva",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // STEPPER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            StepItem(
                number = "1",
                text = "Vehículo",
                isActive = uiState.currentStep >= 1,
                isCompleted = uiState.currentStep > 1
            )

            StepItem(
                number = "2",
                text = "Horario",
                isActive = uiState.currentStep >= 2,
                isCompleted = uiState.currentStep > 2
            )

            StepItem(
                number = "3",
                text = "Plaza",
                isActive = uiState.currentStep >= 3,
                isCompleted = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    when (uiState.currentStep) {

                        // STEP 1
                        1 -> {

                            StepOne(
                                uiState = uiState,
                                onVehicleSelected = {
                                    viewModel.selectVehicle(it)
                                }
                            )
                        }

                        // STEP 2
                        2 -> {

                            StepTwo(
                                uiState = uiState,
                                onDateSelected = {
                                    viewModel.selectDate(it)
                                },
                                onStartSelected = {
                                    viewModel.selectStartTime(it)
                                },
                                onEndSelected = {
                                    viewModel.selectEndTime(it)
                                }
                            )
                        }

                        // STEP 3
                        3 -> {

                            StepThree(
                                uiState = uiState,
                                onSpotSelected = {
                                    viewModel.selectSpot(it)
                                },
                                onFilterChange = {
                                    viewModel.updateFilter(it)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // BACK
                    OutlinedButton(
                        onClick = {

                            if (uiState.currentStep > 1) {

                                viewModel.updateStep(
                                    uiState.currentStep - 1
                                )
                            }
                        },
                        enabled = uiState.currentStep > 1,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                        Text("ATRÁS")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // NEXT / FINISH
                    Button(
                        onClick = {

                            when (uiState.currentStep) {

                                // STEP 1
                                1 -> {

                                    if (uiState.selectedVehicle == null) {

                                        Toast.makeText(
                                            context,
                                            "Selecciona un vehículo",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        viewModel.updateStep(2)
                                    }
                                }

                                // STEP 2
                                2 -> {

                                    if (
                                        uiState.selectedDate == null ||
                                        uiState.startTime == null ||
                                        uiState.endTime == null
                                    ) {

                                        Toast.makeText(
                                            context,
                                            "Completa todos los campos",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    val now = Calendar.getInstance()

                                    if (
                                        uiState.startTime!!.before(now)
                                    ) {

                                        Toast.makeText(
                                            context,
                                            "La hora de inicio no puede ser anterior a la actual",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@Button
                                    }

                                    val duration =
                                        uiState.endTime!!.timeInMillis -
                                                uiState.startTime!!.timeInMillis

                                    val hours =
                                        duration / (1000 * 60 * 60.0)

                                    when {

                                        hours <= 0 -> {

                                            Toast.makeText(
                                                context,
                                                "La hora de fin debe ser posterior",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        hours > 9 -> {

                                            Toast.makeText(
                                                context,
                                                "La duración máxima es de 9 horas",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        else -> {

                                            viewModel.updateStep(3)
                                        }
                                    }
                                }

                                // STEP 3
                                3 -> {

                                    if (uiState.selectedSpot == null) {

                                        Toast.makeText(
                                            context,
                                            "Selecciona una plaza",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        val success =
                                            viewModel.createReservation()

                                        if (!success) {

                                            Toast.makeText(
                                                context,
                                                "La plaza ya no está disponible",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeLKS
                        )
                    ) {

                        if (uiState.isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Text(
                                text =
                                    if (uiState.currentStep < 3)
                                        "SIGUIENTE"
                                    else
                                        "FINALIZAR"
                            )
                        }
                    }
                }
            }
        }
    }
}
