package com.lksnext.ParkingAVillegas.ui.components.reservation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.ui.state.ReservationUiState
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StepTwo(
    uiState: ReservationUiState,
    onDateSelected: (Calendar) -> Unit,
    onStartSelected: (Calendar) -> Unit,
    onEndSelected: (Calendar) -> Unit
) {

    val context = LocalContext.current

    val dateFormat = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    )

    val timeFormat = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    )

    Column {

        Text(
            text = "Selecciona fecha y hora",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    val calendar = Calendar.getInstance()

                    DatePickerDialog(
                        context,
                        { _, y, m, d ->

                            val newDate = Calendar.getInstance().apply {

                                set(y, m, d)

                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            onDateSelected(newDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {

                        datePicker.minDate = System.currentTimeMillis()

                        datePicker.maxDate =
                            System.currentTimeMillis() +
                                    (7 * 24 * 60 * 60 * 1000)

                    }.show()
                }
        ) {

            OutlinedTextField(
                value = uiState.selectedDate?.let {
                    dateFormat.format(it.time)
                } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = {
                    Text("Fecha *")
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = OrangeLKS
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {

                        if (uiState.selectedDate == null) {

                            Toast.makeText(
                                context,
                                "Selecciona primero una fecha",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val base =
                                uiState.startTime ?: Calendar.getInstance()

                            TimePickerDialog(
                                context,
                                { _, h, m ->

                                    val cal =
                                        (uiState.selectedDate.clone() as Calendar).apply {

                                            set(Calendar.HOUR_OF_DAY, h)
                                            set(Calendar.MINUTE, m)
                                        }

                                    onStartSelected(cal)
                                },
                                base.get(Calendar.HOUR_OF_DAY),
                                base.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    }
            ) {

                OutlinedTextField(
                    value = uiState.startTime?.let {
                        timeFormat.format(it.time)
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = {
                        Text("Hora inicio")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {

                        if (uiState.selectedDate == null) {

                            Toast.makeText(
                                context,
                                "Selecciona primero una fecha",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val base =
                                uiState.endTime ?: Calendar.getInstance()

                            TimePickerDialog(
                                context,
                                { _, h, m ->

                                    val cal =
                                        (uiState.selectedDate.clone() as Calendar).apply {

                                            set(Calendar.HOUR_OF_DAY, h)
                                            set(Calendar.MINUTE, m)
                                        }

                                    onEndSelected(cal)
                                },
                                base.get(Calendar.HOUR_OF_DAY),
                                base.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    }
            ) {

                OutlinedTextField(
                    value = uiState.endTime?.let {
                        timeFormat.format(it.time)
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = {
                        Text("Hora fin")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.startTime != null && uiState.endTime != null) {

            val durationMillis =
                uiState.endTime.timeInMillis -
                        uiState.startTime.timeInMillis

            val hours =
                durationMillis / (1000 * 60 * 60.0)

            if (hours > 0) {

                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Duración:",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                "%.1f horas",
                                hours
                            ),
                            color = OrangeLKS,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}