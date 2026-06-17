package com.lksnext.ParkingAVillegas.ui.components.reservation

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.data.static.ParkingData
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.validation.ReservationValidator
import com.lksnext.ParkingAVillegas.validation.VehicleValidator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun EditReservationDialog(
    res: Reservation,
    user: User?,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {

    var selectedVehiclePlate by remember {
        mutableStateOf(res.vehiclePlate)
    }

    var startTime by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = res.startTime
            }
        )
    }

    var endTime by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = res.endTime
            }
        )
    }

    val currentSpot = ParkingData.allSpots.find {
        it.id == res.spotId
    }

    val context = LocalContext.current

    val timeFormat =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Reserva",
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Vehículo:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                user?.vehiculos?.forEach { vehicle ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedVehiclePlate = vehicle.plate
                            }
                    ) {

                        RadioButton(
                            selected =
                                selectedVehiclePlate == vehicle.plate,
                            onClick = {
                                selectedVehiclePlate = vehicle.plate
                            }
                        )

                        Text("${vehicle.brand} - ${vehicle.plate}")
                    }
                }

                HorizontalDivider()

                Text(
                    "Horario:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = {

                            TimePickerDialog(
                                context,
                                { _, hour, minute ->

                                    val newStart =
                                        (startTime.clone() as Calendar).apply {

                                            set(
                                                Calendar.HOUR_OF_DAY,
                                                hour
                                            )

                                            set(
                                                Calendar.MINUTE,
                                                minute
                                            )
                                        }

                                    val validation =
                                        ReservationValidator
                                            .validateReservationTime(
                                                newStart,
                                                endTime
                                            )

                                    if (!validation.isValid) {

                                        Toast.makeText(
                                            context,
                                            validation.errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        startTime = newStart
                                    }
                                },
                                startTime.get(Calendar.HOUR_OF_DAY),
                                startTime.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            "In: ${
                                timeFormat.format(startTime.time)
                            }"
                        )
                    }

                    OutlinedButton(
                        onClick = {

                            TimePickerDialog(
                                context,
                                { _, hour, minute ->

                                    val newEnd =
                                        (endTime.clone() as Calendar).apply {

                                            set(
                                                Calendar.HOUR_OF_DAY,
                                                hour
                                            )

                                            set(
                                                Calendar.MINUTE,
                                                minute
                                            )
                                        }

                                    val validation =
                                        ReservationValidator
                                            .validateReservationTime(
                                                startTime,
                                                newEnd
                                            )

                                    if (!validation.isValid) {

                                        Toast.makeText(
                                            context,
                                            validation.errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        endTime = newEnd
                                    }
                                },
                                endTime.get(Calendar.HOUR_OF_DAY),
                                endTime.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            "Out: ${
                                timeFormat.format(endTime.time)
                            }"
                        )
                    }
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    val timeValidation = ReservationValidator.validateReservationTime(
                        startTime,
                        endTime
                    )

                    if (!timeValidation.isValid) {
                        Toast.makeText(
                            context,
                            timeValidation.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val selectedVehicle =
                        user
                            ?.vehiculos
                            ?.find {
                                it.plate == selectedVehiclePlate
                            }

                    if (selectedVehicle == null) {

                        Toast.makeText(
                            context,
                            "Vehículo inválido",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    if (currentSpot == null) {

                        Toast.makeText(
                            context,
                            "No se encontró la plaza",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val validation =
                        VehicleValidator
                            .validateVehicleForSpot(
                                selectedVehicle,
                                currentSpot.type
                            )

                    if (!validation.isValid) {

                        Toast.makeText(
                            context,
                            validation.errorMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    onConfirm(
                        res.copy(
                            vehiclePlate = selectedVehiclePlate,
                            startTime = startTime.timeInMillis,
                            endTime = endTime.timeInMillis
                        )
                    )
                }
            ) {

                Text("GUARDAR")
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("CANCELAR")
            }
        }
    )
}