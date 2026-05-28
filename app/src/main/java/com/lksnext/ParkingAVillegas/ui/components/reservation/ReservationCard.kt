package com.lksnext.ParkingAVillegas.ui.components.reservation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.Reservation
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReservationCard(
    res: Reservation,
    isHistory: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    val dateFormat =
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

    val timeFormat =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    color =
                        if (isHistory)
                            Color.Gray.copy(0.1f)
                        else
                            OrangeLKS.copy(0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(56.dp)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Default.LocalParking,
                            null,
                            tint =
                                if (isHistory)
                                    Color.Gray
                                else
                                    OrangeLKS
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        "Plaza ${res.spotId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = dateFormat.format(Date(res.date)),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text =
                            "${timeFormat.format(Date(res.startTime))} - ${
                                timeFormat.format(Date(res.endTime))
                            }",
                        color =
                            if (isHistory)
                                Color.Gray
                            else
                                OrangeLKS,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        res.vehiclePlate,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color =
                            if (isHistory)
                                Color(0xFFEEEEEE)
                            else
                                Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                        Text(
                            text =
                                if (isHistory)
                                    "Completada"
                                else
                                    "Confirmada",
                            color =
                                if (isHistory)
                                    Color.Gray
                                else
                                    Color(0xFF2E7D32),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }

            if (!isHistory) {

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = onDelete
                    ) {

                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Red
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            "Eliminar",
                            color = Color.Red
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeLKS
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text("Editar")
                    }
                }
            }
        }
    }
}